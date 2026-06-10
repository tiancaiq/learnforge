package com.learnforge.pay.third.wx;

import cn.hutool.json.JSONObject;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learnforge.common.utils.JsonUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.pay.sdk.constants.PayConstants;
import com.learnforge.pay.third.IPayService;
import com.learnforge.pay.third.model.PayStatusResponse;
import com.learnforge.pay.third.model.PrepayResponse;
import com.learnforge.pay.third.model.RefundResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service(PayConstants.WX_CHANNEL_CODE)
@RequiredArgsConstructor
public class WxPayService implements IPayService {

    private final WxPayClient wxPayClient;

    @Override
    public PrepayResponse createPrepayOrder(String title, String orderNo, Integer amount) {
        // 1. Request Address
        String requestPath = "https://api.mch.weixin.qq.com/v3/pay/transactions/native";
        // 2. Request Parameters
        ObjectNode baseParam = wxPayClient.baseParam(true, true, false)
                .put("out_trade_no", orderNo)
                .put("description", title);
        baseParam.putObject("amount").put("total", amount);

        // 3. Send Request
        String responseJson = wxPayClient.doPostJson(requestPath, baseParam);

        // 4. Parse
        JSONObject result = JsonUtils.parseObj(responseJson);

        String codeUrl = result.getStr("code_url");
        if (codeUrl != null) {
            // 4.1. Response Normal, Return Payment Link
            return PrepayResponse.builder()
                    .success(true)
                    .payUrl(codeUrl)
                    .build();
        } else {
            // 4.2. Response Exception, Return Exception Information
            return PrepayResponse.builder()
                    .success(false)
                    .code(result.getStr("code"))
                    .msg(result.getStr("message"))
                    .detail(result.getStr("detail"))
                    .build();
        }
    }

    @Override
    public PayStatusResponse queryPayOrderStatus(String payOrderNo) {
        // 1. Request Address
        String requestPath = "https://api.mch.weixin.qq.com/v3/pay/transactions/out-trade-no/" + payOrderNo;
        // 2. Send request
        String responseJson = wxPayClient.doGetJson(requestPath, true);
        System.out.println("responseJson = " + responseJson);
        // 3. Parse response
        JSONObject result = JsonUtils.parseObj(responseJson);
        String code = result.getStr("code");
        String message = result.getStr("message");
        LocalDateTime successTime = result.getLocalDateTime("success_time", LocalDateTime.now());
        // 3.1. Request Exception
        if(StringUtils.isNotBlank(code)){
            return PayStatusResponse.builder().success(false).code(code).msg(message).build();
        }
        // 3.2. Request Successful
        return PayStatusResponse.builder()
                .success(true)
                .payOrderNo(payOrderNo)
                .payStatus(parsePayStatus(result.getStr("trade_state")))
                .msg(result.getStr("trade_state_desc"))
                .totalAmount(result.getJSONObject("amount").getInt("total"))
                .successTime(successTime)
                .build()
                ;
    }

    @Override
    public RefundResponse refundOrder(String payOrderNo, String refundOrderNo, Integer refundAmount, Integer totalAmount) {
        // 1. Request Address
        String requestPath = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds";
        // 2. Prepare request parameters
        ObjectNode baseParam = wxPayClient.baseParam(false, false, true)
                .put("out_trade_no", payOrderNo)
                .put("out_refund_no", refundOrderNo);
        baseParam.putObject("amount")
                .put("refund", refundAmount)
                .put("total", totalAmount)
                .put("currency", "CNY")
        ;
        // 3. Send Request
        String responseJson = wxPayClient.doPostJson(requestPath, baseParam);
        // 4. Parse
        JSONObject result = JsonUtils.parseObj(responseJson);
        String code = result.getStr("code");
        String message = result.getStr("message");
        // 4.1. Request Exception
        if(StringUtils.isNotBlank(code)){
            return RefundResponse.builder().success(false).code(code).msg(message).build();
        }
        // 4.2. Request Successful
        JSONObject amount = result.getJSONObject("amount");
        String status = result.getStr("status");
        return RefundResponse.builder()
                .success(true)
                .channel(result.getStr("channel"))
                .status(parseRefundStatus(status))
                .amount(amount == null ? 0 : amount.getInt("refund"))
                .build();
    }

    @Override
    public RefundResponse queryRefundStatus(String orderNo, String refundOrderNo) {
        // 1. Request Address
        String requestPath = "https://api.mch.weixin.qq.com/v3/refund/domestic/refunds/" + refundOrderNo;
        // 2. Send request
        String responseJson = wxPayClient.doGetJson(requestPath, false);
        // 3. Parse response
        JSONObject result = JsonUtils.parseObj(responseJson);
        String code = result.getStr("code");
        String message = result.getStr("message");
        // 3.1. Request Exception
        if(StringUtils.isNotBlank(code)){
            return RefundResponse.builder().success(false).code(code).msg(message).build();
        }
        // 3.2. Request Successful
        String status = result.getStr("status");
        JSONObject amount = result.getJSONObject("amount");
        return RefundResponse.builder()
                .success(true)
                .channel(result.getStr("channel"))
                .status(parseRefundStatus(status))
                .amount(amount == null ? 0 : amount.getInt("refund"))
                .build();
    }

    private Integer parseRefundStatus(String status) {
        switch (status) {
            case "PROCESSING":
                return 1;
            case "SUCCESS":
                return 2;
            default:
                return 3;
        }
    }

    private Integer parsePayStatus(String tradeState) {
        switch (tradeState) {
            case "REFUND":
            case "CLOSED":
            case "REVOKED":
                return 2;
            case "SUCCESS":
                return 3;
            default:
                return 1;
        }
    }

}
