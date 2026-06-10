package com.learnforge.learning.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.concurrent.DelayQueue;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
class DelayTaskTest {
    @Test
    void testDelayQueue() throws InterruptedException {

        DelayQueue<DelayTask<String>> queue = new DelayQueue<>();
        log.info("start");
        queue.add(new DelayTask<>("delay task3", Duration.ofSeconds(3)));
        queue.add(new DelayTask<>("delay task1", Duration.ofSeconds(1)));
        queue.add(new DelayTask<>("delay task2", Duration.ofSeconds(2)));

        while (true) {
            DelayTask<String> task = queue.take();
            log.info("start extction{}",task.getData());
        }
    }
}