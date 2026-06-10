package com.learnforge.pay.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alipay.easysdk.factory.Factory;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.autoconfigure.redisson.annotations.Lock;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.DateUtils;
import com.learnforge.common.utils.JsonUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.pay.domain.po.PayOrder;
import com.learnforge.pay.domain.po.RefundOrder;
import com.learnforge.pay.sdk.constants.PayConstants;
import com.learnforge.pay.sdk.constants.PayErrorInfo;
import com.learnforge.pay.sdk.dto.PayResultDTO;
import com.learnforge.pay.sdk.dto.RefundResultDTO;
import com.learnforge.pay.service.INotifyService;
import com.learnforge.pay.service.IPayOrderService;
import com.learnforge.pay.service.IRefundOrderService;
import com.learnforge.pay.third.ali.AliPayService;
import com.learnforge.pay.third.model.RefundStatus;
import com.learnforge.pay.third.wx.config.WxPayProperties;
import com.wechat.pay.contrib.apache.httpclient.auth.Verifier;
import com.wechat.pay.contrib.apache.httpclient.cert.CertificatesManager;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import com.wechat.pay.contrib.apache.httpclient.exception.ParseException;
import com.wechat.pay.contrib.apache.httpclient.exception.ValidationException;
import com.wechat.pay.contrib.apache.httpclient.notification.Notification;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationHandler;
import com.wechat.pay.contrib.apache.httpclient.notification.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotifyServiceImpl implements INotifyService {
    private final CertificatesManager certificatesManager;
    private final WxPayProperties properties;
    private final IPayOrderService payOrderService;
    private final RabbitMqHelper rabbitMqHelper;
    private final IRefundOrderService refundOrderService;

    @Override
    public void handleWxPayNotify(NotificationRequest request) {
        log.info("Received WeChat Pay notification: {}", request.getBody());
        // 1. Try to validate WeChat notification request parameters, security check
        Notification notification = checkWxNotifyRequest(request);
        if (notification == null || !StringUtils.equals(notification.getEventType(), "TRANSACTION.SUCCESS")) {
            // Notification type is not payment success, do not process
            return;
        }
        // 2. Parse the encrypted callback data
        String decryptData = notification.getDecryptData();
        JSONObject data = JsonUtils.parseObj(decryptData);

        // 3. Get basic information for business validation
        // 3.1. Transaction serial number
        Long tradingOrderNo = data.getLong("out_trade_no");
        // 3.2. Order amount
        JSONObject amountObject = data.getJSONObject("amount");
        Integer amount = amountObject == null ? null : amountObject.getInt("total");
        // 3.3. Order payment time
        LocalDateTime successTime = data.getLocalDateTime("success_time", LocalDateTime.now());

        // 4. Validate notification data, mainly business validation and idempotency check
        PayOrder payOrder = checkNotifyData(tradingOrderNo, amount, successTime);
        if (payOrder == null) return;

        // 5. Notify business service
        rabbitMqHelper.send(
                MqConstants.Exchange.PAY_EXCHANGE,
                MqConstants.Key.PAY_SUCCESS,
                PayResultDTO.builder()
                        .payChannel(payOrder.getPayChannelCode())
                        .payOrderNo(payOrder.getPayOrderNo())
                        .bizOrderId(payOrder.getBizOrderNo())
                        .successTime(successTime)
                        .build());
    }


    @Override
    public void handleWxPayRefundNotify(NotificationRequest request) {
        log.info("Received WeChat refund notification: {}", request.getBody());
        // 1. Try to validate WeChat notification request parameters, security check
        Notification notification = checkWxNotifyRequest(request);
        if (notification == null || !StringUtils.equalsAny(
                notification.getEventType(), "REFUND.SUCCESS", "REFUND.ABNORMAL", "REFUND.CLOSED")) {
            // Notification type error, return directly
            log.debug("WeChat Refund Notification Type Abnormal");
            return;
        }

        // 2. Parsing Notification Data
        String decryptData = notification.getDecryptData();
        JSONObject data = JsonUtils.parseObj(decryptData);
        // 2.1. Refund Order Number
        Long refundOrderNo = data.getLong("out_refund_no");
        if (refundOrderNo == null) {
            log.error("WeChat Notification Data is Incorrect, Missing Refund Order Number");
            throw new BadRequestException("WeChat Notification Data is Incorrect, Missing Refund Order Number");
        }
        // 2.2. Refund Status
        String statusStr = notification.getEventType();
        RefundStatus status = handleWxRefundStatus(statusStr);

        // 3. Idempotency Check
        RefundOrder refundOrder = checkRefundData(refundOrderNo, status, null);
        if (refundOrder == null) return;

        // 4. Send MQ Notification to Business Side
        rabbitMqHelper.send(
                MqConstants.Exchange.PAY_EXCHANGE,
                MqConstants.Key.REFUND_CHANGE,
                RefundResultDTO.builder()
                        .status(status == RefundStatus.SUCCESS ? RefundResultDTO.SUCCESS : RefundResultDTO.FAILED)
                        .bizPayOrderId(refundOrder.getBizOrderNo())
                        .bizRefundOrderId(refundOrder.getBizRefundOrderNo())
                        .refundChannel(refundOrder.getRefundChannel())
                        .refundOrderNo(refundOrder.getRefundOrderNo())
                        .msg(data.getStr("msg"))
                        .build()
        );
    }

    @Override
    public void handleAliPayNotify(Map<String, String> request) {
        log.error("Received Alipay Notification Information, request = {}", request);
        // 1. Determine if it is a Successful Notification
        String tradeStatus = request.get("trade_status");
        if (!StrUtil.equals(tradeStatus, "TRADE_SUCCESS")) {
            // Notification Result is Not Successful, Directly End
            return;
        }
        // 2. Verify Signature
        checkAliNotifyRequest(request);

        // 3. Get basic information for business validation
        // 3.1. Transaction serial number
        String out_trade_no = request.get("out_trade_no");
        Long tradingOrderNo = StringUtils.isNumeric(out_trade_no) ? Long.valueOf(out_trade_no) : null;
        // 3.2. Order Amount, Alipay's Returned Order Amount Should be Multiplied by 100
        String total_amount = request.get("total_amount");
        Integer amount = StringUtils.isNotBlank(total_amount) ? AliPayService.transferStringAmount2Int(total_amount) : null;
        // 3.3. Order payment time
        String success_time = request.get("notify_time");
        LocalDateTime successTime = StringUtils.isBlank(success_time) ?
                LocalDateTime.now() : DateUtils.parse(success_time, DateUtils.DEFAULT_DATE_TIME_FORMAT);

        // 4. Validate notification data, mainly business validation and idempotency check
        PayOrder payOrder = checkNotifyData(tradingOrderNo, amount, successTime);
        if (payOrder == null) return;

        // 5. Notify business service
        rabbitMqHelper.send(
                MqConstants.Exchange.PAY_EXCHANGE,
                MqConstants.Key.PAY_SUCCESS,
                PayResultDTO.builder()
                        .payOrderNo(payOrder.getPayOrderNo())
                        .payChannel(payOrder.getPayChannelCode())
                        .bizOrderId(payOrder.getBizOrderNo())
                        .successTime(successTime)
                        .build()
        );
    }


    private RefundOrder checkRefundData(Long refundOrderNo, RefundStatus status, String channel) {
        // 1. Query Refund Order
        RefundOrder refundOrder = refundOrderService.queryByRefundOrderNo(refundOrderNo);
        // 2. Check if empty
        if (refundOrder == null) {
            throw new BadRequestException("Notification Data is Incorrect, Refund Order Does Not Exist");
        }
        // 3. Determine if the Status Has Changed
        if (status.equalsValue(refundOrder.getStatus())) {
            // Order Status Has Not Changed, Belongs to Duplicate Notification
            return null;
        }
        // 4. Update Refund Order Status
        boolean success = refundOrderService.lambdaUpdate()
                .set(RefundOrder::getStatus, status.getValue())
                .set(StringUtils.isNotBlank(channel), RefundOrder::getRefundChannel, channel)
                .eq(RefundOrder::getId, refundOrder.getId())
                .eq(RefundOrder::getStatus, refundOrder.getStatus())
                .update();
        if(!success){
            return null;
        }
        return refundOrder;
    }

    private RefundStatus handleWxRefundStatus(String statusStr) {
        if (StringUtils.equalsAny(statusStr, "REFUND.CLOSED", "REFUND.ABNORMAL")) {
            return RefundStatus.FAILED;
        }
        if ("REFUND.SUCCESS".equals(statusStr)) {
            return RefundStatus.SUCCESS;
        }
        return RefundStatus.UN_KNOWN;
    }


    private void checkAliNotifyRequest(Map<String, String> request) {
        try {
            Boolean isValid = Factory.Payment.Common().verifyNotify(request);
            if (!isValid) {
                // Notification Signature is Incorrect
                log.error("Alipay Notification Callback Signature Verification Failed, request = {}", request);
                throw new BadRequestException(PayErrorInfo.INVALID_NOTIFY_PARAM);
            }
        } catch (Exception e) {
            log.error("Failed to Obtain Alipay Verification Tool", e);
            throw new CommonException("Failed to Obtain Alipay Verification Tool", e);
        }
    }


    @Nullable
    private Notification checkWxNotifyRequest(NotificationRequest request) {
        try {
            Verifier verifier = certificatesManager.getVerifier(properties.getMchId());
            String apiV3Key = properties.getApiV3Key();
            NotificationHandler handler = new NotificationHandler(verifier, apiV3Key.getBytes(StandardCharsets.UTF_8));
            // Verify Signature and Parse Request Body
            return handler.parse(request);

        } catch (NotFoundException e) {
            log.error("Merchant {}'s Verification Certificate Not Found", properties.getMchId(), e);
            return null;
        } catch (ValidationException e) {
            log.error("WeChat Callback Result Verification Failed", e);
            throw new BadRequestException(400, "WeChat Callback Result Verification Failed", e);
        } catch (ParseException e) {
            log.error("WeChat Callback Result Parsing Failed", e);
            throw new BadRequestException(400, "WeChat Callback Result Parsing Failed", e);
        } catch (RuntimeException e) {
            log.error("WeChat Callback Result Processing Failed", e);
            throw new BadRequestException(400, "WeChat Callback Result Processing Failed", e);
        }
    }

    @Nullable
    @Lock(name = PayConstants.RedisKeyFormatter.PAY_NOTIFY)
    private PayOrder checkNotifyData(Long tradingOrderNo, Integer amount, LocalDateTime successTime) {
        // 1. Data Non-Empty Validation
        if (tradingOrderNo == null || amount == null) {
            throw new BadRequestException(400, PayErrorInfo.INVALID_NOTIFY_PARAM);
        }
        log.info("Payment Callback Notification: payOrderNo = {}, amount = {}", tradingOrderNo, amount);

        // 2. Query Transaction Order, Idempotency Check
        PayOrder payOrder = payOrderService.queryByPayOrderNo(tradingOrderNo);
        // 2.1. Non-Empty Validation
        if (payOrder == null) {
            log.error("Payment Callback Notification's Payment Order {} Does Not Exist", tradingOrderNo);
            return null;
        }
        // 2.2. If the Payment Order is Already Paid or Closed, It Cannot Be Processed Again
        if (payOrder.success() || payOrder.closed()) {
            log.error("Payment Callback Notification's Payment Order {} is Already Paid or Closed, Belongs to Duplicate Notification", tradingOrderNo);
            return null;
        }

        // 3. Validate Payment Amount
        if (!payOrder.getAmount().equals(amount)) {
            // Amount is Incorrect
            log.error("Payment Callback Notification's Amount is Incorrect, Payment Order Number: {}, Notification Amount: {}, Actual Amount: {}",
                    tradingOrderNo, amount, payOrder.getAmount());
            throw new BizIllegalException("WeChat Notification Amount is Incorrect");
        }

        // 4. Update Order Status, and Perform Idempotency Based on Optimistic Lock
        boolean success = payOrderService.markPayOrderSuccess(payOrder.getId(), successTime);
        if (!success) {
            // If the Update Fails, It Indicates a Duplicate Notification
            return null;
        }

        return payOrder;
    }

}
