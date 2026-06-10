package com.learnforge.pay.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.pay.domain.po.PayOrder;
import com.learnforge.pay.sdk.dto.PayApplyDTO;
import com.learnforge.pay.sdk.dto.PayResultDTO;

import java.time.LocalDateTime;

/**
 * <p>
 * Payment Order Service Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
public interface IPayOrderService extends IService<PayOrder> {

    String applyPayOrder(PayApplyDTO payApplyDTO);

    PayOrder queryByBizOrderNo(Long bizOrderNo);

    PayResultDTO queryPayResult(Long bizOrderId);

    PayOrder queryByPayOrderNo(Long payOrderNo);

    boolean markPayOrderSuccess(Long id, LocalDateTime successTime);

    PageDTO<PayOrder> queryPayingOrderByPage(int page, int size);

    void checkPayOrder(PayOrder payOrder);
}
