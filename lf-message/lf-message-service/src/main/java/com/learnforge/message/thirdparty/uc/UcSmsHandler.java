package com.learnforge.message.thirdparty.uc;

import com.learnforge.api.dto.sms.SmsInfoDTO;
import com.learnforge.message.domain.po.MessageTemplate;
import com.learnforge.message.thirdparty.ISmsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * uCloud Platform SMS sending function
 */
@Service("uCloud")
@Slf4j
public class UcSmsHandler implements ISmsHandler {
    @Override
    public void send(SmsInfoDTO platformSmsInfoDTO, MessageTemplate template) {
        //Third-party SMS verification code sending
        log.info("SMS sending success...");
        log.info("platformSmsInfoDTO：{}", platformSmsInfoDTO);
    }
}
