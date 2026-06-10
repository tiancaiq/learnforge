package com.learnforge.pay.third;

import com.learnforge.pay.third.model.PayStatusResponse;
import com.learnforge.pay.third.model.PrepayResponse;
import com.learnforge.pay.third.model.RefundResponse;

/**
 * Unified Payment Service Interface
 */
public interface IPayService {

    PrepayResponse createPrepayOrder(String title, String orderNo, Integer amount);

    PayStatusResponse queryPayOrderStatus(String payOrderNo);

    RefundResponse refundOrder(String payOrderNo, String refundOrderNo, Integer refundAmount, Integer totalAmount);

    RefundResponse queryRefundStatus(String orderNo, String refundOrderNo);
}
