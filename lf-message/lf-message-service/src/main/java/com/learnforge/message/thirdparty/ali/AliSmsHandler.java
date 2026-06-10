package com.learnforge.message.thirdparty.ali;

import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponseBody;
import com.learnforge.api.dto.sms.SmsInfoDTO;
import com.learnforge.common.utils.JsonUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.message.domain.po.MessageTemplate;
import com.learnforge.message.thirdparty.ISmsHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service("aliYun")
@Slf4j
@RequiredArgsConstructor
public class AliSmsHandler implements ISmsHandler {

    private final AsyncClient asyncClient;

    @Override
    public void send(SmsInfoDTO platformSmsInfoDTO, MessageTemplate template) {
        log.info("aliYun platform, prepare to send SMS: {}", platformSmsInfoDTO);
        // 1. Prepare request parameters
        String phones = StringUtils.join(",", platformSmsInfoDTO.getPhones());
        SendSmsRequest request = SendSmsRequest.builder()
                .phoneNumbers(phones)
                .templateCode(template.getThirdTemplateCode())
                .signName(template.getSignName())
                .templateParam(JsonUtils.toJsonStr(platformSmsInfoDTO.getTemplateParams()))
                .build();
        // 2. Send SMS verification code
        CompletableFuture<SendSmsResponse> responseFuture = asyncClient.sendSms(request);
        log.info("aliYun Platform, SMS sending request sent...");
        // 3. Result Handling
        responseFuture.thenAccept(response -> {
            SendSmsResponseBody body = response.getBody();
            String code = body.getCode();
            if("OK".equals(code)){
                log.debug("aliYun SMS sending success, phone number: {}", phones);
            }else{
                log.error("aliYun SMS sending failed, code: {}, reason: {}", code, body.getMessage());
            }
        });
        responseFuture.exceptionally(e -> {
            log.error("aliYun SMS sending exception", e);
            return null;
        });
    }
}
