package com.learnforge.promotion.service.impl;

import com.learnforge.promotion.domain.po.Coupon;
import com.learnforge.promotion.domain.po.ExchangeCode;
import com.learnforge.promotion.mapper.ExchangeCodeMapper;
import com.learnforge.promotion.service.IExchangeCodeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.promotion.utils.CodeUtil;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.learnforge.promotion.constants.PromotionConstants.COUPON_CODE_MAP_KEY;
import static com.learnforge.promotion.constants.PromotionConstants.COUPON_CODE_SERIAL_KEY;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-02
 */
@Service
public class ExchangeCodeServiceImpl extends ServiceImpl<ExchangeCodeMapper, ExchangeCode> implements IExchangeCodeService {
    private final StringRedisTemplate redisTemple;
    private BoundValueOperations<String, String> serialOps;

    public ExchangeCodeServiceImpl(StringRedisTemplate redisTemplate) {
        this.redisTemple = redisTemplate;
        this.serialOps = redisTemplate.boundValueOps(COUPON_CODE_SERIAL_KEY);
    }

    @Override
    @Async("generateExchangeCodeExecutor")
    public void asyncGenerateCode(Coupon coupon) {

        Integer totalNum = coupon.getTotalNum();
        // get auto serial from redis
        Long result = serialOps.increment(totalNum);
        if (result == null) {
            return;
        }
        int maxSerialNum = result.intValue();
        List<ExchangeCode> list = new ArrayList<>(totalNum);
        for (int serialNum = maxSerialNum - totalNum; serialNum < maxSerialNum; serialNum++) {


            //2. generate code
            String code = CodeUtil.generateCode(serialNum, coupon.getId());

            ExchangeCode e = new ExchangeCode();
            e.setCode(code);
            e.setId(serialNum);
            e.setExchangeTargetId(coupon.getId());
            e.setExpiredTime(coupon.getIssueEndTime());
            list.add(e);

        }


        //3. db
        saveBatch(list);
    }

    @Override
    public boolean updateExchangeMark(long serialNum, boolean mark) {
        Boolean boo = redisTemple.opsForValue().setBit(COUPON_CODE_MAP_KEY, serialNum, mark);


        return boo != null && boo;
    }
}
