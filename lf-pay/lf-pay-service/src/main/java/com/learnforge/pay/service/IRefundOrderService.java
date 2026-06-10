package com.learnforge.pay.service;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.pay.domain.po.RefundOrder;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.pay.sdk.dto.RefundApplyDTO;
import com.learnforge.pay.sdk.dto.RefundResultDTO;

/**
 * <p>
 * Refund Order Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
public interface IRefundOrderService extends IService<RefundOrder> {

    RefundResultDTO applyRefund(RefundApplyDTO refundApplyDTO);

    RefundResultDTO queryRefundResult(Long bizRefundOrderId);

    RefundOrder queryByRefundOrderNo(Long refundOrderNo);

    PageDTO<RefundOrder> queryRefundingOrderByPage(int pageNo, int size);

    void checkRefundOrder(RefundOrder refundOrder);

    RefundResultDTO queryRefundDetail(Long bizRefundOrderId);
}
