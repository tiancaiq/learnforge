package com.learnforge.common.autoconfigure.mq;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import static com.learnforge.common.constants.Constant.REQUEST_ID_HEADER;

@Slf4j
public class RabbitMqHelper {

    private final RabbitTemplate rabbitTemplate;
    private final MessagePostProcessor processor = new BasicIdMessageProcessor();
    private final ThreadPoolTaskExecutor executor;

    public RabbitMqHelper(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        executor = new ThreadPoolTaskExecutor();
        //Configure Core Thread Count
        executor.setCorePoolSize(10);
        //Configure Maximum Thread Count
        executor.setMaxPoolSize(15);
        //Configure Queue Size
        executor.setQueueCapacity(99999);
        //Configure Thread Name Prefix in Thread Pool
        executor.setThreadNamePrefix("mq-async-send-handler");

        // Set rejection policy: how to handle new tasks when pool has reached max size
        // CALLER_RUNS: do not execute tasks in a new thread, but have the caller's thread execute
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //Execute Initialization
        executor.initialize();
    }

    /**
     * Send message according to exchange and routingKey
     */
    public <T> void send(String exchange, String routingKey, T t) {
        log.debug("Prepare to send message, exchange: {}, RoutingKey: {}, message: {}", exchange, routingKey, t);
        // 1. Set message identifier for message confirmation, message send failure directly throws exception, to be handled by caller
        String id = UUID.randomUUID().toString(true);
        CorrelationData correlationData = new CorrelationData(id);
        // 2. Set send timeout time to 500 milliseconds
        rabbitTemplate.setReplyTimeout(500);
        // 3. Send message, and set message id
        rabbitTemplate.convertAndSend(exchange, routingKey, t, processor, correlationData);
    }

    /**
     * Send message according to exchange and routingKey, and can set delay time
     */
    public <T> void sendDelayMessage(String exchange, String routingKey, T t, Duration delay) {
        // 1. Set message identifier for message confirmation, message send failure directly throws exception, to be handled by caller
        String id = UUID.randomUUID().toString(true);
        CorrelationData correlationData = new CorrelationData(id);
        // 2. Set send timeout time to 500 milliseconds
        rabbitTemplate.setReplyTimeout(500);
        // 3. Send message, and set message id
        rabbitTemplate.convertAndSend(exchange, routingKey, t, new DelayedMessageProcessor(delay), correlationData);
    }


    /**
     * Send message according to exchange and routingKey asynchronously, and specify a delay time
     *
     * @param exchange exchange
     * @param routingKey routing key
     * @param t data
     * @param <T> data type
     */
    public <T> void sendAsync(String exchange, String routingKey, T t, Long time) {
        String requestId = MDC.get(REQUEST_ID_HEADER);
        CompletableFuture.runAsync(() -> {
            try {
                MDC.put(REQUEST_ID_HEADER, requestId);
                // Send delayed message
                if (time != null && time > 0) {
                    sendDelayMessage(exchange, routingKey, t, Duration.ofMillis(time));
                } else {
                    send(exchange, routingKey, t);
                }
            } catch (Exception e) {
                log.error("Push message exception, t: {},", t, e);
            }
        }, executor);
    }


    /**
     * Send message asynchronously according to exchange and routingKey
     *
     * @param exchange exchange
     * @param routingKey routing key
     * @param t data
     * @param <T> data type
     */
    public <T> void sendAsync(String exchange, String routingKey, T t) {
        sendAsync(exchange, routingKey, t, null);
    }

}
