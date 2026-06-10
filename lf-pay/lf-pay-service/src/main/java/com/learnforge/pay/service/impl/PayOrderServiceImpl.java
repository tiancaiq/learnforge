package com.learnforge.pay.service.impl;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.autoconfigure.redisson.annotations.Lock;
import com.learnforge.common.autoconfigure.redisson.enums.LockStrategy;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.pay.constants.NotifyStatus;
import com.learnforge.pay.domain.po.PayOrder;
import com.learnforge.pay.mapper.PayOrderMapper;
import com.learnforge.pay.sdk.constants.PayConstants;
import com.learnforge.pay.sdk.constants.PayErrorInfo;
import com.learnforge.pay.sdk.dto.PayApplyDTO;
import com.learnforge.pay.sdk.dto.PayResultDTO;
import com.learnforge.pay.service.IPayOrderService;
import com.learnforge.pay.third.IPayService;
import com.learnforge.pay.third.model.PayStatus;
import com.learnforge.pay.third.model.PayStatusResponse;
import com.learnforge.pay.third.model.PrepayResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Map;

import static com.learnforge.pay.sdk.constants.PayErrorInfo.*;

/**
 * <p>
 * Payment Order Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PayOrderServiceImpl extends ServiceImpl<PayOrderMapper, PayOrder> implements IPayOrderService {

    @Resource
    private Map<String, IPayService> payServiceChannels;
    private final RabbitMqHelper rabbitMqHelper;

    @Override
    @Lock(name = PayConstants.RedisKeyFormatter.PAY_APPLY, leaseTime = 3, autoUnlock = false)
    public String applyPayOrder(PayApplyDTO payApplyDTO) {
        log.debug("Prepare to Create Payment Order, Business Order Number: {}", payApplyDTO.getBizOrderNo());
        // 1. Select Payment Channel
        IPayService payService = payServiceChannels.get(payApplyDTO.getPayChannelCode());
        if (payService == null) {
            log.error("User's Selected Payment Channel Does Not Exist, Business Order Number: {}", payApplyDTO.getBizOrderNo());
            throw new BadRequestException(INVALID_PAY_CHANNEL);
        }

        // 2. Idempotency Check
        PayOrder payOrder = checkIdempotent(payApplyDTO);
        if (StringUtils.isNotBlank(payOrder.getQrCodeUrl())) {
            log.debug("Payment Link Already Exists, No Need to Recreate, Directly Return");
            return payOrder.getQrCodeUrl();
        }

        // 3. Need to Generate New Payment Link, Call Third Party, Complete Payment Order Creation
        PrepayResponse prepayResponse = payService.createPrepayOrder(
                payApplyDTO.getOrderInfo(), payOrder.getPayOrderNo().toString(), payOrder.getAmount());

        // 4. Update Payment Link Information to Database
        updatePayResult2DB(prepayResponse, payOrder.getId());

        if (!prepayResponse.isSuccess()) {
            log.error("Pre-Creation of Payment Order Failed, Details: {}", prepayResponse.getDetail());
            throw new BizIllegalException(PayErrorInfo.CREATE_PAY_ORDER_FAILED);
        }
        // 5. Return Payment Link
        log.debug("Payment Order Creation Successful, Return Payment Link: {}", prepayResponse.getPayUrl());
        return prepayResponse.getPayUrl();
    }

    private void updatePayResult2DB(PrepayResponse prepayResponse, Long payOrderId) {
        try {
            lambdaUpdate()
                    .set(prepayResponse.isSuccess(), PayOrder::getQrCodeUrl, prepayResponse.getPayUrl())
                    .set(prepayResponse.isSuccess(), PayOrder::getStatus, PayStatus.WAIT_BUYER_PAY.getValue())
                    .set(!prepayResponse.isSuccess(), PayOrder::getResultCode, prepayResponse.getCode())
                    .set(!prepayResponse.isSuccess(), PayOrder::getResultMsg, prepayResponse.getMsg())
                    .eq(PayOrder::getId, payOrderId)
                    .update();
        } catch (Exception e) {
            log.error("Exception Occurred When Updating Payment Order Result to Database", e);
        }
    }

    private PayOrder buildPayOrder(PayApplyDTO payApplyDTO) {
        // 1. Data conversion
        PayOrder payOrder = BeanUtils.toBean(payApplyDTO, PayOrder.class);
        // 2. Initialize Data
        payOrder.setNotifyTimes(0);
        payOrder.setNotifyStatus(NotifyStatus.UN_CALL.getValue());
        payOrder.setPayOverTime(LocalDateTime.now().plusMinutes(120L));
        payOrder.setStatus(PayStatus.NOT_COMMIT.getValue());
        return payOrder;
    }

    private PayOrder checkIdempotent(PayApplyDTO payApplyDTO) {
        // 1. First Query Payment Order
        PayOrder oldOrder = queryByBizOrderNo(payApplyDTO.getBizOrderNo());
        // 2. Determine if It Exists
        if (oldOrder == null) {
            // Payment Order Does Not Exist, It is the First Time, Write New Payment Order and Return
            PayOrder payOrder = buildPayOrder(payApplyDTO);
            payOrder.setPayOrderNo(IdWorker.getId());
            save(payOrder);
            return payOrder;
        }
        // 3. Old Order Already Exists, Determine if Payment is Successful
        if (PayStatus.TRADE_SUCCESS.equalsValue(oldOrder.getStatus())) {
            // Payment Already Successful, Throw Exception
            throw new BizIllegalException(PAY_ORDER_ALREADY_PAY_CODE, PAY_ORDER_ALREADY_PAY);
        }
        // 4. Old Order Already Exists, Determine if It is Already Closed
        if (PayStatus.TRADE_CLOSED.equalsValue(oldOrder.getStatus())) {
            // Already Closed, Throw Exception
            throw new BizIllegalException(PAY_ORDER_ALREADY_CLOSE_CODE, PAY_ORDER_ALREADY_CLOSE);
        }
        // 5. Old Order Already Exists, Determine if Payment Channel is Consistent
        if (!StringUtils.equals(oldOrder.getPayChannelCode(), payApplyDTO.getPayChannelCode())) {
            // Payment Channel Inconsistent, Need to Reset Data and Reapply for Payment Order
            PayOrder payOrder = buildPayOrder(payApplyDTO);
            payOrder.setId(oldOrder.getId());
            payOrder.setQrCodeUrl("");
            updateById(payOrder);
            payOrder.setPayOrderNo(oldOrder.getPayOrderNo());
            return payOrder;
        }
        // 6. Old Order Already Exists, and It May Be Unpaid or Unsubmitted, and Payment Channel is Consistent, Directly Return Old Data
        return oldOrder;
    }

    @Override
    public PayOrder queryByBizOrderNo(Long bizOrderNo) {
        return lambdaQuery()
                .eq(PayOrder::getBizOrderNo, bizOrderNo)
                .one();
    }

    @Override
    public PayResultDTO queryPayResult(Long bizOrderNo) {
        // 1. Query Payment Order
        PayOrder payOrder = queryByBizOrderNo(bizOrderNo);
        if (payOrder == null) {
            throw new BizIllegalException(PAY_ORDER_NOT_FOUND);
        }
        // 2. Determine Payment Status
        if (payOrder.success()) {
            // 2.1. Payment Successful
            return PayResultDTO.builder()
                    .payOrderNo(payOrder.getPayOrderNo())
                    .successTime(payOrder.getPaySuccessTime())
                    .payChannel(payOrder.getPayChannelCode())
                    .build();
        }
        // 2.2. Unpaid
        if (payOrder.notCommit() || payOrder.waitBuyerPay()) {
            return PayResultDTO.builder()
                    .status(PayStatus.WAIT_BUYER_PAY.getValue())
                    .build();
        }
        // 2.2. Payment Failed
        return PayResultDTO.builder()
                .status(PayStatus.TRADE_CLOSED.getValue())
                .msg(payOrder.getResultMsg())
                .build();
    }

    @Override
    public PayOrder queryByPayOrderNo(Long payOrderNo) {
        return lambdaQuery().eq(PayOrder::getPayOrderNo, payOrderNo).one();
    }

    @Override
    public boolean markPayOrderSuccess(Long id, LocalDateTime successTime) {
        return lambdaUpdate()
                .set(PayOrder::getStatus, PayStatus.TRADE_SUCCESS.getValue())
                .set(PayOrder::getNotifyStatus, NotifyStatus.CALLING.getValue())
                .set(PayOrder::getPaySuccessTime, successTime)
                .eq(PayOrder::getId, id)
                // Optimistic Lock Judgment for Payment Status
                .in(PayOrder::getStatus, PayStatus.NOT_COMMIT.getValue(), PayStatus.WAIT_BUYER_PAY.getValue())
                .update();
    }

    @Override
    public PageDTO<PayOrder> queryPayingOrderByPage(int pageNo, int size) {
        // 1. Pagination and Sorting Conditions
        Page<PayOrder> page = new Page<>(pageNo, size);
        page.addOrder(new OrderItem("id", true));
        // 2. Query
        Page<PayOrder> result = lambdaQuery()
                .eq(PayOrder::getStatus, PayStatus.WAIT_BUYER_PAY.getValue())
                .page(page);
        return PageDTO.of(result);
    }

    @Override
    @Lock(name = PayConstants.RedisKeyFormatter.PAY_ORDER_CHECK_TASK, lockStrategy = LockStrategy.SKIP_AFTER_RETRY_TIMEOUT)
    public void checkPayOrder(PayOrder payOrder) {
        // 1. Select Payment Channel
        IPayService payService = payServiceChannels.get(payOrder.getPayChannelCode());
        if (payService == null) {
            log.error("Payment Channel Does Not Exist, Business Order Number: {}", payOrder.getBizOrderNo());
            // Abnormal Order, Need to Close Payment Order
            closeOrder(payOrder.getId());
            return;
        }
        // 2. Determine if Order is Timeout
        if (payOrder.getPayOverTime().isBefore(LocalDateTime.now())) {
            log.debug("Payment Order {} is Timeout, Close Order", payOrder.getPayOrderNo());
            closeOrder(payOrder.getId());
            return;
        }
        // 3. Query Payment Status
        PayStatusResponse response = payService.queryPayOrderStatus(payOrder.getPayOrderNo().toString());
        Integer payStatus = response.getPayStatus();
        // 3.1. Determine if Query Failed or Payment is in Progress
        if (!response.isSuccess() || PayStatus.WAIT_BUYER_PAY.equalsValue(payStatus)) {
            // Query Exception or Payment in Progress, End
            return;
        }
        // 3.2. Determine if Payment Status Has Changed
        if (payStatus.equals(payOrder.getStatus())) {
            // Payment Status Has Not Changed
            return;
        }
        // 3.3. Status is payment success or failure, directly update order status
        updatePayStatus2DB(response, payOrder.getId());
        // 3.4. Check if status is success, success requires sending MQ message notification
        if (PayStatus.TRADE_SUCCESS.equalsValue(response.getPayStatus())) {
            rabbitMqHelper.send(
                    MqConstants.Exchange.PAY_EXCHANGE,
                    MqConstants.Key.PAY_SUCCESS,
                    PayResultDTO.builder()
                            .payOrderNo(payOrder.getPayOrderNo())
                            .bizOrderId(payOrder.getBizOrderNo())
                            .payChannel(payOrder.getPayChannelCode())
                            .successTime(response.getSuccessTime())
                            .build()
            );
        }
    }

    private void updatePayStatus2DB(PayStatusResponse response, Long id) {
        try {
            lambdaUpdate()
                    .set(PayOrder::getStatus, response.getPayStatus())
                    .set(PayOrder::getResultCode, response.getCode() == null ? "" : response.getCode())
                    .set(PayOrder::getResultMsg, response.getMsg() == null ? "" : response.getMsg())
                    .eq(PayOrder::getId, id)
                    .update();
        } catch (Exception e) {
            log.error("Exception Occurred When Updating Payment Order Result to Database", e);
        }
    }

    private void closeOrder(Long id) {
        PayOrder payOrder = new PayOrder();
        payOrder.setId(id);
        payOrder.setStatus(PayStatus.TRADE_CLOSED.getValue());
        updateById(payOrder);
    }
}
