package com.learnforge.pay.controller;

import com.learnforge.common.utils.StringUtils;
import com.learnforge.pay.sdk.constants.PayConstants;
import com.learnforge.pay.service.INotifyService;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.stream.Collectors;

@ApiIgnore
@RestController
@RequestMapping("notify")
@RequiredArgsConstructor
public class NotifyController {

    private final INotifyService notifyService;
    /**
     * Alipay callback interface
     * @param httpRequest Callback parameters
     * @return Processing result
     */
    @PostMapping(PayConstants.ALI_CHANNEL_CODE)
    public ResponseEntity<String> handleAliPayNotify(HttpServletRequest httpRequest){
        // 1. Process request parameters as a Map
        Map<String, String[]> parameterMap = httpRequest.getParameterMap();
        Map<String, String> request = parameterMap.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, e -> StringUtils.join(",", e.getValue())));
        // 2. Process notification
        notifyService.handleAliPayNotify(request);
        return ResponseEntity.ok("success");
    }

    /**
     * WeChat Pay callback interface
     * @return Processing result
     */
    @PostMapping(PayConstants.WX_CHANNEL_CODE)
    public ResponseEntity<Object> handleWxPayNotify(HttpEntity<String> httpEntity){
        try {
            // 1. Write request information into NotificationRequest
            NotificationRequest request = transformHttpEntityToNotificationRequest(httpEntity);
            // 2. Process notification
            notifyService.handleWxPayNotify(request);
        } catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("code", "FAIL", "message", e.getMessage()));
        }
        // 3. Return success
        return ResponseEntity.ok().build();
    }

    /**
     * WeChat Pay callback interface
     * @return Processing result
     */
    @PostMapping("/refund/" + PayConstants.WX_CHANNEL_CODE)
    public ResponseEntity<Object> handleWxPayRefundNotify(HttpEntity<String> httpEntity){
        try {
            // 1. Write request information into NotificationRequest
            NotificationRequest request = transformHttpEntityToNotificationRequest(httpEntity);
            // 2. Process notification
            notifyService.handleWxPayRefundNotify(request);
        } catch (Exception e){
            return ResponseEntity.status(500).body(Map.of("code", "FAIL", "message", e.getMessage()));
        }
        // 3. Return success
        return ResponseEntity.ok().build();
    }


    private NotificationRequest transformHttpEntityToNotificationRequest(HttpEntity<String> httpEntity) {
        HttpHeaders headers = httpEntity.getHeaders();
        // 1. Build notification request information
        return new NotificationRequest.Builder()
                .withSerialNumber(headers.getFirst("Wechatpay-Serial"))
                .withNonce(headers.getFirst("Wechatpay-Nonce"))
                .withTimestamp(headers.getFirst("Wechatpay-Timestamp"))
                .withSignature(headers.getFirst("Wechatpay-Signature"))
                .withBody(httpEntity.getBody())
                .build();
    }

}
