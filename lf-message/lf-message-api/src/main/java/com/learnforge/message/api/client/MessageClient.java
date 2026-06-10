package com.learnforge.message.api.client;

import com.learnforge.message.domain.dto.SmsInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("message-service")
public interface MessageClient {

    /**
     * Send SMS synchronously
     * @param smsInfoDTO SMS related information
     */
    @PostMapping("message")
    void sendMessage(@RequestBody SmsInfoDTO smsInfoDTO);

}
