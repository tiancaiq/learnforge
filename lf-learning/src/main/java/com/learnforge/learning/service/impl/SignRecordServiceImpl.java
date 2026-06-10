package com.learnforge.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.BooleanUtils;
import com.learnforge.common.utils.DateUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.learning.constants.RedisConstants;
import com.learnforge.learning.domain.vo.SignResultVO;
import com.learnforge.learning.mq.message.SignInMessage;
import com.learnforge.learning.service.ISignRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SignRecordServiceImpl implements ISignRecordService {

    private final StringRedisTemplate redisTemplate;
    private final RabbitMqHelper mqHelper;
    @Override
    public SignResultVO addSignRecords() {
        // 1. check in
        Long userId = UserContext.getUser();
        LocalDate now = LocalDate.now();
        String key = RedisConstants.SIGN_RECORD_KEY_PREFIX + userId + now.format(DateUtils.SIGN_DATE_SUFFIX_FORMATTER);

        // offset
        int offset = now.getDayOfMonth() - 1;

        // redis
        Boolean exits = redisTemplate.opsForValue().setBit(key, offset, true);
        if(BooleanUtils.isTrue(exits)){
            throw new BizIllegalException("can't add sign record");
        }
        //2. calcuate continue check in time
        int signDays = countSignDay(key, now.getDayOfMonth());
        // 3. Calculate the continuous sign-in reward.
        int rewarPoint = 0;
        switch(signDays){
            case 7:
                rewarPoint = 10;
                break;
            case 14:
                rewarPoint = 20;
                break;
            case 28:
                rewarPoint = 40;
                break;
        }
        // 4. Publish the points record asynchronously.
        mqHelper.send(MqConstants.Exchange.LEARNING_EXCHANGE, MqConstants.Key.SIGN_IN, SignInMessage.of(userId, rewarPoint+1));
        SignResultVO vo = new SignResultVO();
        vo.setSignDays(signDays);
        vo.setRewardPoints(rewarPoint);
        return vo;
    }

    private int countSignDay(String key, int len) {

        List<Long> result = redisTemplate.opsForValue()
                .bitField(key, BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(len)).valueAt(0));

        if (CollUtil.isEmpty(result)) {
            return 0;
        }
        int num = result.get(0).intValue();

        int count = 0;

        while ((num & 1) == 1) {
            count++;
            // remove last digit
            num >>>= 1;
        }

        return count;
    }
}
