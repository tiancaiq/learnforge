package com.learnforge.message.thirdparty.tencent;

import com.learnforge.api.dto.sms.SmsInfoDTO;
import com.learnforge.message.domain.po.MessageTemplate;
import com.learnforge.message.thirdparty.ISmsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service("tencent")
@Slf4j
public class TencentSmsHandler implements ISmsHandler {
    @Override
    public void send(SmsInfoDTO platformSmsInfoDTO, MessageTemplate template) {
        //Third-party SMS verification code sending
        log.info("tencent Platform, SMS sending success...");
        log.info("platformSmsInfoDTO：{}", platformSmsInfoDTO);
    }
}
