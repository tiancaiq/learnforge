package com.learnforge.trade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolTaskExecutor sendRefundRequestExecutor(){
        ThreadPoolTaskExecutor refundExecutor = new ThreadPoolTaskExecutor();
        //Configure Core Thread Count
        refundExecutor.setCorePoolSize(4);
        //Configure Maximum Thread Count
        refundExecutor.setMaxPoolSize(20);
        //Configure Queue Size
        refundExecutor.setQueueCapacity(10000);
        //Configure Thread Name Prefix in Thread Pool
        refundExecutor.setThreadNamePrefix("pd-user-async-service-");
        // Executed by caller thread
        refundExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        refundExecutor.initialize();
        return refundExecutor;
    }
}
