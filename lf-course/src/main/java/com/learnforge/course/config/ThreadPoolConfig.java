package com.learnforge.course.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class ThreadPoolConfig {

    @Bean("taskExecutor")
    public Executor asyncServiceExecutor() {
        log.info("start asyncServiceExecutor");
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
        return executor;
    }

}
