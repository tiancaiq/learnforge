package com.learnforge.promotion.config;


import com.learnforge.promotion.utils.MyLockAspect;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
public class PromotionConfig {

    @Bean
    public Executor generateExchangeCodeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        //core pool
        executor.setCorePoolSize(2);
        //max pool
        executor.setMaxPoolSize(5);
        //3.queue size
        executor.setQueueCapacity(200);
        //threadname
        executor.setThreadNamePrefix("exchange-code-handler-");
        //5 rejection policy
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return  executor;
    }

//    @Bean
//    public MyLockAspect myLockAspect(RedissonClient redissonClient) {
//        return new MyLockAspect(redissonClient);
//    }

    @Bean
    public Executor discountSolutionExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        //core pool
        executor.setCorePoolSize(12);
        //max pool
        executor.setMaxPoolSize(12);
        //3.queue size
        executor.setQueueCapacity(99999);
        //threadname
        executor.setThreadNamePrefix("discount-solution-calculator-");
        //5 rejection policy
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        return  executor;
    }
}
