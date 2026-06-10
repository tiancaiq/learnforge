package com.learnforge.promotion.service.impl;

import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.autoconfigure.redisson.annotations.Lock;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.UserContext;
import com.learnforge.promotion.constants.PromotionConstants;
import com.learnforge.promotion.domain.dto.UserCouponDTO;
import com.learnforge.promotion.domain.po.Coupon;
import com.learnforge.promotion.domain.po.ExchangeCode;
import com.learnforge.promotion.domain.po.UserCoupon;
import com.learnforge.promotion.enums.ExchangeCodeStatus;
import com.learnforge.promotion.mapper.CouponMapper;
import com.learnforge.promotion.mapper.UserCouponMapper;
import com.learnforge.promotion.service.IExchangeCodeService;
import com.learnforge.promotion.service.IUserCouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.promotion.utils.CodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-07
 */
@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl extends ServiceImpl<UserCouponMapper, UserCoupon> implements IUserCouponService {


    private final CouponMapper couponMapper;

    private final IExchangeCodeService codeService;
    private final StringRedisTemplate redisTemplate;

    private final RabbitMqHelper mqHelper;

    @Override
    @Lock(name =  "lock:coupon:#{couponId}")
    public void receiveCoupon(Long couponId) {
        // 1. query coupon
        Coupon coupon = queryCouponBycache(couponId);
        if(coupon ==null){
            throw new BadRequestException("coupon not exist");
        }
        //2. check realse time
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getIssueBeginTime()) || now.isAfter(coupon.getIssueEndTime())) {
            throw new BadRequestException("coupon begin time is before end time");
        }
        //3. check storage
        if (coupon.getTotalNum() <= 0) {
            throw new BadRequestException("coupon issue num is over");
        }
        Long userId = UserContext.getUser();
        //4. check obtained number for each person
        //4.1 query obtained amount
        String key = PromotionConstants.USER_COUPON_CACHE_KEY_PREFIX + couponId;
        Long count = redisTemplate.opsForHash().increment(key, userId.toString(), 1);
        //4.2 check limit amount
        if(count == null || count > coupon.getUserLimit()){
            if (count != null) {
                redisTemplate.opsForHash().increment(key, userId.toString(), -1);
            }
            throw new BadRequestException("coupon limit exceed");
        }

        //5 deduct coupon storage
        Long remain = redisTemplate.opsForHash().increment(
                PromotionConstants.COUPON_CACHE_KEY_PREFIX + couponId, "totalNum", -1);
        if (remain == null || remain < 0) {
            redisTemplate.opsForHash().increment(key, userId.toString(), -1);
            if (remain != null) {
                redisTemplate.opsForHash().increment(
                        PromotionConstants.COUPON_CACHE_KEY_PREFIX + couponId, "totalNum", 1);
            }
            throw new BadRequestException("coupon issue num is over");
        }


        //6. MQ
        UserCouponDTO uc = new UserCouponDTO();
        uc.setUserId(userId);
        uc.setCouponId(couponId);

        try {
            mqHelper.send(MqConstants.Exchange.PROMOTION_EXCHANGE, MqConstants.Key.COUPON_RECEIVE,uc);
        } catch (RuntimeException e) {
            redisTemplate.opsForHash().increment(key, userId.toString(), -1);
            redisTemplate.opsForHash().increment(
                    PromotionConstants.COUPON_CACHE_KEY_PREFIX + couponId, "totalNum", 1);
            throw e;
        }

    }

    private Coupon queryCouponBycache(Long couponId) {
        String key = PromotionConstants.COUPON_CACHE_KEY_PREFIX+ couponId;
        Map<Object, Object> objMap = redisTemplate.opsForHash().entries(key);
        if ( objMap.isEmpty()) {
            return null;
        }
        Coupon coupon = new Coupon();
        coupon.setId(couponId);
        coupon.setIssueBeginTime(toLocalDateTime(objMap.get("issueBeginTime")));
        coupon.setIssueEndTime(toLocalDateTime(objMap.get("issueEndTime")));
        coupon.setTotalNum(Integer.valueOf(objMap.get("totalNum").toString()));
        coupon.setUserLimit(Integer.valueOf(objMap.get("userLimit").toString()));
        return coupon;
    }

    private LocalDateTime toLocalDateTime(Object epochMilli) {
        return LocalDateTime.ofInstant(
                Instant.ofEpochMilli(Long.parseLong(epochMilli.toString())),
                ZoneId.systemDefault());
    }

    //SPELL

    @Transactional
    @Override
    public void checkAndCreateUserCoupon(UserCouponDTO uc) {

        Coupon coupon = couponMapper.selectById(uc.getCouponId());
        if(coupon ==null){
            throw new BizIllegalException("coupon not exist");
        }


        Integer count = lambdaQuery()
                .eq(UserCoupon::getCouponId, coupon.getId())
                .eq(UserCoupon::getUserId, uc.getUserId())
                .count();

        if (count != null && count >= coupon.getUserLimit()){
            throw new BadRequestException("coupon limit exceed");
        }
        //5. update released +1
        int r = couponMapper.incrIssuNum(coupon.getId());

        if (r == 0){
            throw new BizIllegalException("coupon issue num is over");
        }
        // 6. update user coupon
        saveUserCoupon(coupon, uc.getUserId());
    }

    @Override
    @Transactional()
    public void exchangeCoupon(String code) {
        // parse code
        long serialNum = CodeUtil.parseCode(code);


        //check if redeemed setbit

        boolean exchanged = codeService.updateExchangeMark(serialNum, true);
        if (exchanged) {
            throw new BizIllegalException("coupon exchanged");
        }
        try {
            //3. query code\
            ExchangeCode exchangeCode = codeService.getById(serialNum);
            if (exchangeCode == null) {
                throw new BizIllegalException("coupon code not exist");
            }
                //4. if expired
            LocalDateTime now = LocalDateTime.now();
            if(now.isAfter(exchangeCode.getExpiredTime())){
                throw new BizIllegalException("coupon expired");
            }
                //5. check amount
            //5.1 get coupon
            Coupon coupon = couponMapper.selectById(exchangeCode.getExchangeTargetId());
            if (coupon == null) {
                throw new BizIllegalException("coupon not exist");
            }
            //5.2 get user
            Long userId = UserContext.getUser();
            UserCouponDTO userCoupon = new UserCouponDTO();
            userCoupon.setCouponId(coupon.getId());
            userCoupon.setUserId(userId);
            checkAndCreateUserCoupon(userCoupon);
                //6. update status
            codeService.lambdaUpdate()
                    .set(ExchangeCode::getUserId, userId)
                    .set(ExchangeCode::getStatus, ExchangeCodeStatus.USED)
                    .eq(ExchangeCode::getId, exchangeCode.getId())
                    .update();
        } catch (Exception e) {
            codeService.updateExchangeMark(serialNum, false);
            throw e;
        }
    }

    private void saveUserCoupon(Coupon coupon, Long userId) {
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(coupon.getId());
        LocalDateTime termBeginTime = coupon.getTermBeginTime();
        LocalDateTime termEndTime = coupon.getTermEndTime();
        if (termBeginTime == null){
            termBeginTime = LocalDateTime.now();
            termEndTime = termBeginTime.plusDays(coupon.getTermDays());
        }
        uc.setTermBeginTime(termBeginTime);
        uc.setTermEndTime(termEndTime);
        save(uc);
    }
}
