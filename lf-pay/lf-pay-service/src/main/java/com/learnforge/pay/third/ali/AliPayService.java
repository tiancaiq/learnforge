package com.learnforge.pay.third.ali;

import com.alipay.easysdk.factory.Factory;
import com.alipay.easysdk.kernel.util.ResponseChecker;
import com.alipay.easysdk.payment.common.models.AlipayTradeFastpayRefundQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeQueryResponse;
import com.alipay.easysdk.payment.common.models.AlipayTradeRefundResponse;
import com.alipay.easysdk.payment.common.models.TradeFundBill;
import com.alipay.easysdk.payment.facetoface.models.AlipayTradePrecreateResponse;
import com.learnforge.common.exceptions.CommonException;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.DateUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.pay.sdk.constants.PayConstants;
import com.learnforge.pay.third.CommonPayProperties;
import com.learnforge.pay.third.IPayService;
import com.learnforge.pay.third.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static com.learnforge.pay.sdk.constants.PayConstants.ALI_CHANNEL_CODE;

@Slf4j
@Service(ALI_CHANNEL_CODE)
@RequiredArgsConstructor
public class AliPayService implements IPayService {

    private final CommonPayProperties commonPayProperties;

    @Override
    public PrepayResponse createPrepayOrder(String title, String orderNo, Integer amount) {
        // 1. Dynamically Get Callback Address
        String notifyUrl = commonPayProperties.getNotifyHost() + "/notify/" + PayConstants.ALI_CHANNEL_CODE;

        // 2. Initiate API Call (Example: Create Face-to-Face Payment QR Code)
        AlipayTradePrecreateResponse response = null;
        try {
            response = Factory.Payment.FaceToFace()
                    .asyncNotify(notifyUrl)
                    .preCreate(title, orderNo, transferAmount2String(amount));
        } catch (Exception e) {
            log.error("Alipay Pre-Order Failed, Order ID: {}", orderNo, e);
            throw new CommonException("Alipay Pre-Order Failed", e);
        }
        // 3. Process Response
        PrepayResponse.PrepayResponseBuilder builder = PrepayResponse.builder();
        if (ResponseChecker.success(response)) {
            // 3.1. Response Result is Normal
            builder.success(true).payUrl(response.getQrCode());
        } else {
            // 3.2. Response Result is Abnormal
            builder.success(false).code(response.getCode()).msg(response.getMsg());
        }
        return builder.build();
    }



    @Override
    public PayStatusResponse queryPayOrderStatus(String payOrderNo) {
        // 1. Initiate Request
        AlipayTradeQueryResponse response = null;
        try {
            response = Factory.Payment.Common().query(payOrderNo);
        } catch (Exception e) {
            log.error("Alipay Query Payment Order Status Failed, Order ID: {}", payOrderNo, e);
            throw new CommonException("Alipay Query Payment Order Status Failed", e);
        }
        // 2. Parse
        if (!ResponseChecker.success(response)) {
            // 2.1. Response Result is Abnormal
            return PayStatusResponse.builder().success(false).code(response.getCode()).msg(response.getMsg()).build();
        }
        // 2.2. Response Result is Normal
        String success_time = response.getSendPayDate();
        LocalDateTime successTime = StringUtils.isBlank(success_time) ?
                LocalDateTime.now() : DateUtils.parse(success_time, DateUtils.DEFAULT_DATE_TIME_FORMAT);
        return PayStatusResponse.builder().success(true)
                        .payStatus(PayStatus.valueOf(response.getTradeStatus()).getValue())
                        .payOrderNo(response.getOutTradeNo())
                        .totalAmount(transferStringAmount2Int(response.getTotalAmount()))
                        .successTime(successTime)
                        .build();
    }

    @Override
    public RefundResponse refundOrder(String payOrderNo, String refundOrderNo, Integer refundAmount, Integer totalAmount) {
        // 1. Initiate Request
        AlipayTradeRefundResponse response = null;
        try {
            response = Factory.Payment.Common()
                    .optional("query_options", List.of("refund_detail_item_list"))
                    .optional("out_request_no", refundOrderNo)
                    .refund(payOrderNo, transferAmount2String(refundAmount));
        } catch (Exception e) {
            log.error("Alipay Refund Application Failed, Order ID: {}, Refund Order ID: {}", payOrderNo, refundOrderNo, e);
            throw new CommonException("Alipay Refund Application Failed", e);
        }
        // 2. Parse Response
        if (!ResponseChecker.success(response)) {
            // 2.1. Response Result is Abnormal
            return RefundResponse.builder().success(false).code(response.getSubCode()).msg(response.getSubMsg()).build();
        }
        // 2.2. Response Result is Normal, Get Response Detail Data
        List<TradeFundBill> refundDetailItemList = response.getRefundDetailItemList();
        boolean hasDetail = CollUtils.isEmpty(refundDetailItemList);
        // 2.3. Get Refund Success Indicator
        boolean success = StringUtils.equals(response.getFundChange(), "Y");
        return RefundResponse.builder()
                .success(true)
                .status(success ? RefundStatus.SUCCESS.getValue(): RefundStatus.UN_KNOWN.getValue())
                .channel(hasDetail ? null : refundDetailItemList.get(0).fundChannel)
                .amount(hasDetail ? null : transferStringAmount2Int(refundDetailItemList.get(0).getAmount()))
                .build();
    }

    @Override
    public RefundResponse queryRefundStatus(String orderNo, String refundOrderNo) {
        // 1. Initiate Request
        AlipayTradeFastpayRefundQueryResponse response = null;
        try {
            response = Factory.Payment.Common().queryRefund(orderNo, refundOrderNo);
        } catch (Exception e) {
            log.error("Alipay Query Refund Order Status Failed, Order ID: {}", orderNo, e);
            throw new CommonException("Alipay Query Refund Order Status Failed", e);
        }
        // 2. Parse
        if (ResponseChecker.success(response)) {
            // 2.1. Response Result is Abnormal
            return RefundResponse.builder().success(false).code(response.getCode()).msg(response.getMsg()).build();
        }
        // 2.2. Response Result is Normal
        String refundStatus = response.getRefundStatus();
        boolean refundSuccess = "REFUND_SUCCESS".equals(refundStatus);

        List<TradeFundBill> details = response.getRefundDetailItemList();
        return RefundResponse.builder()
                .success(true)
                .status(refundSuccess ? 2 : 1)
                .channel(refundSuccess ? details.get(0).fundChannel : null)
                .amount(refundSuccess ? transferStringAmount2Int(details.get(0).getAmount()) : 0)
                .build();
    }


    public static int transferStringAmount2Int(String totalAmount) {
        return new BigDecimal(totalAmount).multiply(BigDecimal.valueOf(100)).intValue();
    }
    public static String transferAmount2String(Integer amount) {
        BigDecimal b = new BigDecimal(amount);
        BigDecimal result = b.divide(new BigDecimal(100), new MathContext(2, RoundingMode.HALF_UP));
        return result.toString();
    }
}
