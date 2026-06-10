package com.learnforge.trade.service.impl;

import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.AssertUtils;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.pay.sdk.client.PayClient;
import com.learnforge.pay.sdk.constants.PayType;
import com.learnforge.pay.sdk.dto.PayApplyDTO;
import com.learnforge.pay.sdk.dto.PayChannelDTO;
import com.learnforge.pay.sdk.dto.PayResultDTO;
import com.learnforge.trade.config.TradeProperties;
import com.learnforge.trade.constants.OrderStatus;
import com.learnforge.trade.constants.TradeErrorInfo;
import com.learnforge.trade.domain.dto.OrderDelayQueryDTO;
import com.learnforge.trade.domain.dto.PayApplyFormDTO;
import com.learnforge.trade.domain.po.Order;
import com.learnforge.trade.domain.po.OrderDetail;
import com.learnforge.trade.domain.vo.PayChannelVO;
import com.learnforge.trade.service.IOrderDetailService;
import com.learnforge.trade.service.IOrderService;
import com.learnforge.trade.service.IPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.learnforge.common.constants.MqConstants.Exchange.TRADE_DELAY_EXCHANGE;
import static com.learnforge.common.constants.MqConstants.Key.ORDER_DELAY_KEY;
import static com.learnforge.trade.constants.TradeErrorInfo.ORDER_NOT_EXISTS;

@Slf4j
@Service
@RequiredArgsConstructor
public class PayServiceImpl implements IPayService {

    private final PayClient payClient;
    private final IOrderService orderService;
    private final IOrderDetailService detailService;
    private final TradeProperties tradeProperties;
    private final RabbitMqHelper mqHelper;

    @Override
    public List<PayChannelVO> queryPayChannels() {
        List<PayChannelDTO> list = payClient.listAllPayChannels();
        if (list == null) {
            return CollUtils.emptyList();
        }
        return list.stream()
                .filter(p -> p.getStatus() == 1)
                .map(p -> BeanUtils.copyBean(p, PayChannelVO.class))
                .collect(Collectors.toList());
    }

    @Override
    public String applyPayOrder(PayApplyFormDTO payApply) {
        Long orderId = payApply.getOrderId();
        // 1. Query Order Information
        Order order = orderService.getById(orderId);
        if (order == null) {
            throw new BadRequestException(ORDER_NOT_EXISTS);
        }
        // 2. Determine Order Status
        if (!OrderStatus.NO_PAY.equalsValue(order.getStatus())) {
            // Order has been paid or closed
            throw new BizIllegalException(TradeErrorInfo.ORDER_ALREADY_FINISH);
        }
        // 3. Determine if Order has Timed Out
        if (order.getCreateTime().plusMinutes(tradeProperties.getPayOrderTTLMinutes()).isBefore(LocalDateTime.now())) {
            // Order has timed out, cannot pay
            throw new BizIllegalException(TradeErrorInfo.ORDER_OVER_TIME);
        }
        // 4. Query Order Details
        List<OrderDetail> details = detailService.queryByOrderId(orderId);
        AssertUtils.isNotEmpty(details, ORDER_NOT_EXISTS);

        // 5. Package Order Parameters
        PayApplyDTO payApplyDTO = PayApplyDTO.builder()
                .bizOrderNo(orderId)
                .amount(order.getRealAmount())
                .orderInfo(details.get(0).getName())
                .bizUserId(order.getUserId())
                .payType(PayType.NATIVE.getValue())
                .payChannelCode(payApply.getPayChannelCode())
                .build();
        String url = payClient.applyPayOrder(payApplyDTO);
        // 6. Query Payment Result Asynchronously via Delay Queue
        sendDelayQueryMessage(OrderDelayQueryDTO.init(orderId));
        return url;
    }

    private void sendDelayQueryMessage(OrderDelayQueryDTO message) {
        mqHelper.sendDelayMessage(
                TRADE_DELAY_EXCHANGE,
                ORDER_DELAY_KEY,
                message, Duration.ofMillis(message.removeFirst()));
    }

    @Override
    public void queryPayResult(OrderDelayQueryDTO message) {
        // 1. Get Order Information
        Long orderId = message.getOrderId();
        Order order = orderService.getById(orderId);
        if (order == null) {
            log.error("Order to Query Status: {} does not exist", orderId);
            return;
        }
        // 2. Determine Order Status
        if (!OrderStatus.NO_PAY.equalsValue(order.getStatus())) {
            // Order has been paid or closed, task ends
            return;
        }
        // 3. Query Payment Status
        PayResultDTO payResult = payClient.queryPayResult(orderId);
        int status = payResult.getStatus();
        if(PayResultDTO.SUCCESS != status){
            // 3.1. Payment in Progress or Payment Failed, Need to Retry Query
            if(message.getDelayMillis().size() == 0){
                // Retry Times Exhausted, End
                return;
            }
            // Send Delayed Query Message, Query Payment Status Again
            sendDelayQueryMessage(message);
        }
        // 3.2. Payment Successful
        orderService.handlePaySuccess(payResult);
    }
}
