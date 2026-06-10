package com.learnforge.message.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.learnforge.message.domain.po.SmsThirdPlatform;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@EnableConfigurationProperties(MessageProperties.class)
public class MessageConfig {
    @Bean("asyncNoticeExecutor")
    public Executor asyncNoticeExecutor() {
        log.info("Start initializing thread pool for executing notification tasks...");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //Configure Core Thread Count
        executor.setCorePoolSize(10);
        //Configure Maximum Thread Count
        executor.setMaxPoolSize(15);
        //Configure Queue Size
        executor.setQueueCapacity(99999);
        //Configure Thread Name Prefix in Thread Pool
        executor.setThreadNamePrefix("pd-user-async-service-");

        // Set rejection policy: how to handle new tasks when pool has reached max size
        // CALLER_RUNS: do not execute tasks in a new thread, but have the caller's thread execute
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //Execute Initialization
        executor.initialize();
        log.info("Initialization of thread pool for executing notification tasks completed...");
        return executor;
    }
    @Bean("asyncSmsExecutor")
    public Executor asyncSmsExecutor() {
        log.info("Start initializing thread pool for SMS sending tasks...");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //Configure Core Thread Count
        executor.setCorePoolSize(50);
        //Configure Maximum Thread Count
        executor.setMaxPoolSize(100);
        //Configure Queue Size
        executor.setQueueCapacity(99999);
        //Configure Thread Name Prefix in Thread Pool
        executor.setThreadNamePrefix("pd-user-async-service-");

        // Set rejection policy: how to handle new tasks when pool has reached max size
        // CALLER_RUNS: do not execute tasks in a new thread, but have the caller's thread execute
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //Execute Initialization
        executor.initialize();
        log.info("Initialization of thread pool for SMS sending tasks completed...");
        return executor;
    }

    @Bean
    public Cache<String, List<SmsThirdPlatform>> platformCache(){
        return Caffeine.newBuilder()
                .initialCapacity(1)
                .maximumSize(20_00)
                .expireAfterWrite(Duration.ofMinutes(30))
                .build();
    }
}
