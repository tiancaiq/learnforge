package com.learnforge.promotion.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.*;
import com.learnforge.promotion.constants.PromotionConstants;
import com.learnforge.promotion.domain.dto.CouponFormDTO;
import com.learnforge.promotion.domain.dto.CouponIssueFormDTO;
import com.learnforge.promotion.domain.po.Coupon;
import com.learnforge.promotion.domain.po.CouponScope;
import com.learnforge.promotion.domain.po.UserCoupon;
import com.learnforge.promotion.domain.query.CouponQuery;
import com.learnforge.promotion.domain.vo.CouponPageVO;
import com.learnforge.promotion.domain.vo.CouponVO;
import com.learnforge.promotion.enums.CouponStatus;
import com.learnforge.promotion.enums.ObtainType;
import com.learnforge.promotion.enums.UserCouponStatus;
import com.learnforge.promotion.mapper.CouponMapper;
import com.learnforge.promotion.mapper.UserCouponMapper;
import com.learnforge.promotion.service.ICouponScopeService;
import com.learnforge.promotion.service.ICouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.promotion.service.IExchangeCodeService;
import com.learnforge.promotion.service.IUserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-02
 */
@Service
@RequiredArgsConstructor
public class CouponServiceImpl extends ServiceImpl<CouponMapper, Coupon> implements ICouponService {

    private final ICouponScopeService scopeService ;

    private final IExchangeCodeService codeService ;

    private final IUserCouponService couponService ;
    private final UserCouponMapper userCouponMapper;

    private final StringRedisTemplate redisTemplate ;

    @Override
    @Transactional
    public void saveCoupon(CouponFormDTO dto) {
        // save coupon
        Coupon coupon = BeanUtils.copyBean(dto, Coupon.class);

        save(coupon);
        // save range

        if (!dto.getSpecific()){
            // no range
            return;
        }

        Long couponId = coupon.getId();
        List<Long> scopes = dto.getScopes();
        if(CollUtils.isEmpty(scopes)){
            throw new BadRequestException("scope can not be empty");
        }


        List<CouponScope> list = scopes.stream()
                .map(bizId -> new CouponScope().setBizId(bizId).setCouponId(couponId))
                .collect(Collectors.toList());

        scopeService.saveBatch(list);
    }

    @Override
    public PageDTO<CouponPageVO> queryCouponByPage(CouponQuery query) {
        Integer status = query.getStatus();
        String name = query.getName();
        Integer type = query.getType();

        Page<Coupon> page = lambdaQuery()
                .eq(status != null, Coupon::getStatus, status)
                .eq(type != null, Coupon::getType, type)
                .like(StringUtils.isNotBlank(name), Coupon::getName, name)
                .page(query.toMpPageDefaultSortByCreateTimeDesc());

        List<Coupon> records = page.getRecords();

        if(CollUtils.isEmpty(records)){
            return PageDTO.empty(page);
        }

        List<CouponPageVO> list = BeanUtils.copyList(records, CouponPageVO.class);


        return PageDTO.of(page, list);
    }

    @Override
    @Transactional
    public void beginIusse(CouponIssueFormDTO dto) {
        //1. query coupon
        Coupon coupon = getById(dto.getId());
        if (coupon == null) {
            throw new BadRequestException("coupon is not exist");
        }
        // 2. status
        if (coupon.getStatus() != CouponStatus.DRAFT && coupon.getStatus() != CouponStatus.PAUSE) {
            throw new BizIllegalException("coupon status must be one of DRAFT, PAUSE");
        }

        // 3. if issue now
        LocalDateTime issueBeginTime = dto.getIssueBeginTime();

        LocalDateTime now = LocalDateTime.now();
        boolean isBegin = issueBeginTime == null || !issueBeginTime.isAfter(now);
        // 4. update coupon

        Coupon c = BeanUtils.copyBean(dto, Coupon.class);

        if (isBegin) {
            c.setStatus(CouponStatus.ISSUING);
            c.setIssueBeginTime(now);
        } else {
            c.setStatus(CouponStatus.UN_ISSUE);

        }
        updateById(c);

        if(isBegin){
            coupon.setIssueBeginTime(c.getIssueBeginTime());
            coupon.setIssueEndTime(c.getIssueEndTime());
            cacheCouponInfo(coupon);

        }

        // 5.check if required generate coupon code,
        if(coupon.getObtainWay() == ObtainType.ISSUE && coupon.getStatus() == CouponStatus.DRAFT){
            coupon.setIssueEndTime(c.getIssueEndTime());
            codeService.asyncGenerateCode(coupon);
        }
    }

    private void cacheCouponInfo(Coupon coupon) {
        Map<String,String> map = new HashMap<>(4);
        map.put("issueBeginTime",String.valueOf(DateUtils.toEpochMilli(coupon.getIssueBeginTime())));
        map.put("issueEndTime",String.valueOf(DateUtils.toEpochMilli(coupon.getIssueEndTime())));
        map.put("totalNum",String.valueOf(coupon.getTotalNum() - coupon.getIssueNum()));
        map.put("userLimit",String.valueOf(coupon.getUserLimit()));
        redisTemplate.opsForHash().putAll(PromotionConstants.COUPON_CACHE_KEY_PREFIX + coupon.getId(), map);
    }

    @Override
    public List<CouponVO> queryIssuingCoupons() {
        //1. query issuing coupons
        List<Coupon> coupons = lambdaQuery()
                .eq(Coupon::getStatus, CouponStatus.ISSUING)
                .eq(Coupon::getObtainWay, ObtainType.PUBLIC)
                .list();

        if (CollUtils.isEmpty(coupons)) {
            return CollUtils.emptyList();
        }
        //2.user coupon info
        List<Long> couponIds = coupons.stream().map(Coupon::getId).collect(Collectors.toList());
        //2.1 query user obtain coupon list
        List<UserCoupon> userCoupons = couponService.lambdaQuery()
                .eq(UserCoupon::getUserId, UserContext.getUser())
                .in(UserCoupon::getCouponId, couponIds).list();
        //2.2 count  obtain coupon number
        Map<Long, Long> issuedMap = userCoupons.stream()
                .collect(Collectors.groupingBy(UserCoupon::getCouponId, Collectors.counting()));
        //2.3 count not used
        Map<Long, Long> unusedMap = userCoupons.stream()
                .filter(uc -> uc.getStatus() == UserCouponStatus.UNUSED)
                .collect(Collectors.groupingBy(UserCoupon::getCouponId, Collectors.counting()));
        //3. vo
        List<CouponVO> list = new ArrayList<>(coupons.size());
        for (Coupon c : coupons) {
            CouponVO vo = BeanUtils.copyBean(c, CouponVO.class);
            list.add(vo);
            //3.2 check if can get
            vo.setAvailable(
                    c.getIssueNum() < c.getTotalNum() && issuedMap.getOrDefault(c.getId(), 0L) < c.getUserLimit()
            );
            // 3,3 if can use
            vo.setReceived(unusedMap.getOrDefault(c.getId(), 0L) > 0);
        }
        return list;
    }

    @Override
    @Transactional
    public void pauseIssue(Long id) {

        Coupon coupon = getById(id);
        if (coupon == null) {
            throw new BizIllegalException("Coupon not found");
        }
        if (coupon.getStatus() != CouponStatus.ISSUING) {
            throw new BizIllegalException("Coupon status does not allow this operation");
        }
        coupon.setStatus(CouponStatus.PAUSE);
        updateById(coupon);

        redisTemplate.delete(PromotionConstants.COUPON_CACHE_KEY_PREFIX + id);
    }
}
