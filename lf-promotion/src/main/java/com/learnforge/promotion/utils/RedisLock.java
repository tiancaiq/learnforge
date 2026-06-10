package com.learnforge.promotion.utils;


import com.learnforge.common.utils.BooleanUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RedisLock {

    private final String key;
    private final StringRedisTemplate redisTemplate;

    public boolean tryLock(long leaseTime, TimeUnit unit) {

        String value = Thread.currentThread().getName();
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, value, leaseTime, unit);
        return BooleanUtils.isTrue(success);
    }


    public void unlock() {
        redisTemplate.delete(key);
    }
}
