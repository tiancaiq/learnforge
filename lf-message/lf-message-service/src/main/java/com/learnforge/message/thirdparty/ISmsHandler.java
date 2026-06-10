package com.learnforge.message.thirdparty;

import com.learnforge.api.dto.sms.SmsInfoDTO;
import com.learnforge.message.domain.po.MessageTemplate;

/**
 * Third-party interface integration platform
 */
public interface ISmsHandler {

    /**
     * Send SMS
     */
    void send(SmsInfoDTO platformSmsInfoDTO, MessageTemplate template);


}
