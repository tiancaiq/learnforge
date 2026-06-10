package com.learnforge.message.api.client;

import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.message.domain.dto.SmsInfoDTO;

public class AsyncSmsClient {
    private final RabbitMqHelper mqHelper;

    public AsyncSmsClient(RabbitMqHelper mqHelper) {
        this.mqHelper = mqHelper;
    }

    /**
     * Send SMS based on MQ asynchronously
     * @param smsInfoDTO SMS related information
     */
    public void sendMessage(SmsInfoDTO smsInfoDTO){
        mqHelper.send(MqConstants.Exchange.SMS_EXCHANGE, MqConstants.Key.SMS_MESSAGE, smsInfoDTO);
    }
}
