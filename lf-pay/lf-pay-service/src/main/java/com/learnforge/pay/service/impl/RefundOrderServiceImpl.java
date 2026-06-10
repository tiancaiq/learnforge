package com.learnforge.pay.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.autoconfigure.redisson.annotations.Lock;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.pay.domain.po.PayOrder;
import com.learnforge.pay.domain.po.RefundOrder;
import com.learnforge.pay.mapper.RefundOrderMapper;
import com.learnforge.pay.sdk.constants.PayConstants;
import com.learnforge.pay.sdk.constants.PayErrorInfo;
import com.learnforge.pay.sdk.dto.RefundApplyDTO;
import com.learnforge.pay.sdk.dto.RefundResultDTO;
import com.learnforge.pay.service.IPayOrderService;
import com.learnforge.pay.service.IRefundOrderService;
import com.learnforge.pay.third.IPayService;
import com.learnforge.pay.third.model.RefundResponse;
import com.learnforge.pay.third.model.RefundStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

import static com.learnforge.pay.sdk.constants.PayErrorInfo.INVALID_PAY_CHANNEL;

/**
 * <p>
 * Refund Order Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RefundOrderServiceImpl extends ServiceImpl<RefundOrderMapper, RefundOrder> implements IRefundOrderService {

    private final IPayOrderService payOrderService;

    private final RabbitMqHelper rabbitMqHelper;

    @Resource
    private Map<String, IPayService> payServiceChannels;

    @Override
    public RefundResultDTO applyRefund(RefundApplyDTO refundApplyDTO) {
        log.debug("Prepare to apply for refund, business side refund order number: {}", refundApplyDTO.getBizRefundOrderNo());
        // 1. Idempotent Check
        RefundOrder refundOrder = checkIdempotent(refundApplyDTO);
        if (refundOrder == null) {
            // Null indicates refund is in progress, no need to reapply, return false
            return RefundResultDTO.running().msg("Refund in Progress").build();
        }
        // 2. Get Payment Channel
        IPayService payService = payServiceChannels.get(refundOrder.getPayChannelCode());
        if (payService == null) {
            log.error("Refund Exception, Payment Channel Does Not Exist, Business Order Number: {}", refundApplyDTO.getBizOrderNo());
            throw new BadRequestException(INVALID_PAY_CHANNEL);
        }
        // 3. Attempt Refund
        RefundResponse refundResponse = payService.refundOrder(
                refundOrder.getPayOrderNo().toString(), refundOrder.getRefundOrderNo().toString(),
                refundOrder.getRefundAmount(), refundOrder.getTotalAmount());
        // 4. Update Refund Result to Database
        updateRefundStatus(refundResponse, refundOrder.getId());
        // 5. Return Refund Application Result
        RefundResultDTO refundResultDTO = transferRefundResult(refundResponse);
        refundResultDTO.setBizRefundOrderId(refundOrder.getBizRefundOrderNo());
        refundResultDTO.setBizPayOrderId(refundOrder.getBizOrderNo());
        refundResultDTO.setRefundOrderNo(refundOrder.getRefundOrderNo());
        return refundResultDTO;
    }

    private void updateRefundStatus(RefundResponse refundResponse, Long id) {
        try {
            lambdaUpdate()
                    .set(refundResponse.getSuccess(), RefundOrder::getStatus, refundResponse.getStatus())
                    .set(refundResponse.getAmount() != null, RefundOrder::getRefundAmount, refundResponse.getAmount())
                    .set(refundResponse.getChannel() != null, RefundOrder::getRefundChannel, refundResponse.getChannel())
                    .set(RefundOrder::getResultCode, refundResponse.getCode() == null ? "" : refundResponse.getCode())
                    .set(RefundOrder::getResultMsg, refundResponse.getMsg() == null ? "" : refundResponse.getMsg())
                    .eq(RefundOrder::getId, id)
                    .update();
        } catch (Exception e) {
            log.error("Exception Occurred Updating Refund Order Status", e);
        }
    }

    @Lock(name = PayConstants.RedisKeyFormatter.REFUND_APPLY, leaseTime = 10, autoUnlock = false)
    private RefundOrder checkIdempotent(RefundApplyDTO refundApplyDTO) {
        // 1. Query Refund Order Corresponding Payment Order
        PayOrder payOrder = payOrderService.queryByBizOrderNo(refundApplyDTO.getBizOrderNo());
        if (payOrder == null) {
            // Payment Order is Empty, Cannot Refund
            throw new BizIllegalException(PayErrorInfo.PAY_ORDER_NOT_FOUND);
        }
        // 2. Check if Already Paid Successfully
        if (!payOrder.success()) {
            // Order Not Paid Yet, Cannot Refund
            throw new BizIllegalException(PayErrorInfo.PAY_ORDER_NOT_SUCCESS);
        }

        // 3. Check if There is Already a Refund Order for Current Order
        RefundOrder oldRefundOrder = queryByBizRefundOrder(refundApplyDTO.getBizRefundOrderNo());
        // 3.1. Check if Empty
        if (oldRefundOrder == null) {
            // This Order is First Refund, Need to Generate New Refund Order
            RefundOrder refundOrder = BeanUtils.toBean(refundApplyDTO, RefundOrder.class);
            refundOrder.setRefundOrderNo(IdWorker.getId());
            refundOrder.setIsSplit(payOrder.getAmount().equals(refundApplyDTO.getRefundAmount()));
            refundOrder.setPayOrderNo(payOrder.getPayOrderNo());
            refundOrder.setTotalAmount(payOrder.getAmount());
            refundOrder.setPayChannelCode(payOrder.getPayChannelCode());
            save(refundOrder);
            return refundOrder;
        }

        // 3.2. Check if Refund Already Successful, If Successful Cannot Refund
        if (oldRefundOrder.success()) {
            throw new BizIllegalException(PayErrorInfo.REPEAT_REFUND_ORDER);
        }

        // 3.3. Check if Refund Already Failed, If Failed Directly End
        if (oldRefundOrder.failed()) {
            throw new BizIllegalException(PayErrorInfo.REFUND_FAILED);
        }

        // 3.4. Refund Request Not Submitted, Resubmit
        if (oldRefundOrder.notCommit()) {
            // Need to Update Refund Data First
            oldRefundOrder.setRefundAmount(refundApplyDTO.getRefundAmount());
            updateById(oldRefundOrder);
            return oldRefundOrder;
        }

        // 3.5. Refund is in Progress, Do Nothing
        return null;
    }

    private RefundOrder queryByBizRefundOrder(Long bizRefundOrderId) {
        return lambdaQuery()
                .eq(RefundOrder::getBizRefundOrderNo, bizRefundOrderId)
                .one();
    }

    @Override
    public RefundResultDTO queryRefundResult(Long bizRefundOrderId) {
        // 1. Query Refund Order
        RefundOrder refundOrder = queryByBizRefundOrder(bizRefundOrderId);
        // 2. Check if empty
        if (refundOrder == null) {
            return null;
        }
        // 3. Check if Refund Order Refund is Successful
        if (refundOrder.success()) {
            return RefundResultDTO.success()
                    .refundOrderNo(refundOrder.getRefundOrderNo())
                    .bizPayOrderId(refundOrder.getBizOrderNo())
                    .bizRefundOrderId(refundOrder.getBizRefundOrderNo())
                    .refundChannel(refundOrder.getRefundChannel())
                    .build();
        }
        // 4. Check if Refund Order Refund is Failed
        if (refundOrder.failed()) {
            return RefundResultDTO.failed()
                    .msg(refundOrder.getResultMsg())
                    .refundOrderNo(refundOrder.getRefundOrderNo())
                    .build();
        }
        // 5. Refund Status is Unknown, Need to Query Third Party, Choose Query Channel
        IPayService payService = payServiceChannels.get(refundOrder.getPayChannelCode());
        if (payService == null) {
            log.error("Refund Exception, Payment Channel Does Not Exist, Business Order Number: {}", bizRefundOrderId);
            throw new BadRequestException(INVALID_PAY_CHANNEL);
        }
        // 6. Query Third Party
        RefundResponse refundResponse = payService.queryRefundStatus(
                refundOrder.getPayOrderNo().toString(), refundOrder.getRefundOrderNo().toString());
        // 6.1. Update Database Refund Order Status
        updateRefundStatus(refundResponse, refundOrder.getId());

        // 6.2. Return Result
        RefundResultDTO refundResultDTO = transferRefundResult(refundResponse);
        refundResultDTO.setRefundOrderNo(refundOrder.getRefundOrderNo());
        refundResultDTO.setBizRefundOrderId(bizRefundOrderId);
        return refundResultDTO;
    }

    @Override
    public RefundOrder queryByRefundOrderNo(Long refundOrderNo) {
        return lambdaQuery()
                .eq(RefundOrder::getRefundOrderNo, refundOrderNo)
                .one();
    }

    @Override
    public PageDTO<RefundOrder> queryRefundingOrderByPage(int pageNo, int size) {
        // 1. Pagination and Sorting Conditions
        Page<RefundOrder> page = new Page<>(pageNo, size);
        page.addOrder(new OrderItem("id", true));
        // 2. Query
        Page<RefundOrder> result = lambdaQuery()
                .eq(RefundOrder::getStatus, RefundStatus.UN_KNOWN.getValue())
                .page(page);
        return PageDTO.of(result);
    }

    @Override
    public void checkRefundOrder(RefundOrder refundOrder) {
        // 1. Get Refund Channel
        String payChannelCode = refundOrder.getPayChannelCode();
        IPayService payService = payServiceChannels.get(payChannelCode);
        if (payService == null) {
            log.error("Payment Channel Does Not Exist, Refund Order Number: {}", refundOrder.getId());
            // Abnormal Order, Need to Close Payment Order
            closeOrder(refundOrder.getId());
            return;
        }
        // 2. Query Refund Status
        RefundResponse refundResponse = payService.queryRefundStatus(
                refundOrder.getPayOrderNo().toString(), refundOrder.getRefundOrderNo().toString());

        if (refundResponse.getStatus().equals(refundOrder.getStatus())) {
            // Refund Status Has Not Changed, Do Nothing
            return;
        }

        // 3. Update Database Refund Order Status
        updateRefundStatus(refundResponse, refundOrder.getId());

        // 4. Send MQ Notification to Business Side
        rabbitMqHelper.send(
                MqConstants.Exchange.PAY_EXCHANGE,
                MqConstants.Key.REFUND_CHANGE,
                RefundResultDTO.success()
                        .refundOrderNo(refundOrder.getRefundOrderNo())
                        .bizPayOrderId(refundOrder.getBizOrderNo())
                        .bizRefundOrderId(refundOrder.getBizRefundOrderNo())
                        .refundChannel(refundOrder.getRefundChannel())
                        .build()
        );
    }

    @Override
    public RefundResultDTO queryRefundDetail(Long bizRefundOrderId) {
        // 1. Query Refund Order
        RefundOrder refundOrder = queryByBizRefundOrder(bizRefundOrderId);
        // 2. Check if empty
        if (refundOrder == null) {
            throw new BadRequestException(PayErrorInfo.REFUND_ORDER_NOT_FOUND);
        }
        // 3. Return results
        return RefundResultDTO.builder()
                .status(refundOrder.getStatus())
                .msg(refundOrder.getResultMsg())
                .bizRefundOrderId(bizRefundOrderId)
                .bizPayOrderId(refundOrder.getBizOrderNo())
                .payOrderNo(refundOrder.getPayOrderNo())
                .refundOrderNo(refundOrder.getRefundOrderNo())
                .payChannel(refundOrder.getRefundChannel())
                .build();
    }

    private void closeOrder(Long id) {
        RefundOrder refundOrder = new RefundOrder();
        refundOrder.setId(id);
        refundOrder.setStatus(RefundStatus.FAILED.getValue());
        updateById(refundOrder);
    }

    private RefundResultDTO transferRefundResult(RefundResponse refundResponse) {
        if (!refundResponse.getSuccess()) {
            return RefundResultDTO.failed()
                    .msg(refundResponse.getMsg()).refundChannel(refundResponse.getChannel()).build();
        }
        if (refundResponse.refundSuccess()) {
            return RefundResultDTO.success().refundChannel(refundResponse.getChannel()).build();
        }
        return RefundResultDTO.running()
                .msg(refundResponse.getMsg()).refundChannel(refundResponse.getChannel()).build();
    }
}
