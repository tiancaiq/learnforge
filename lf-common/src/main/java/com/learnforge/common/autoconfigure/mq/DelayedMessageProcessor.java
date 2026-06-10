package com.learnforge.common.autoconfigure.mq;

import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;

import java.time.Duration;

public class DelayedMessageProcessor extends BasicIdMessageProcessor {

    private final long delay;

    public DelayedMessageProcessor(Duration delay) {
        this.delay = delay.toMillis();
    }

    @Override
    public Message postProcessMessage(Message message) throws AmqpException {
        // 1. Add message id
        super.postProcessMessage(message);
        // 2. Add delay time
        message.getMessageProperties().setHeader("x-delay", delay);
        return message;
    }
}
