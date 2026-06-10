package com.learnforge.trade.handler;

import com.learnforge.common.constants.MqConstants;
import com.learnforge.pay.sdk.dto.PayResultDTO;
import com.learnforge.pay.sdk.dto.RefundResultDTO;
import com.learnforge.trade.domain.dto.OrderDelayQueryDTO;
import com.learnforge.trade.service.IOrderService;
import com.learnforge.trade.service.IPayService;
import com.learnforge.trade.service.IRefundApplyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayMessageHandler {

    private final IOrderService orderService;
    private final IRefundApplyService refundApplyService;
    private final IPayService payService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "trade.pay.success.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.PAY_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.PAY_SUCCESS
    ))
    public void listenPaySuccess(PayResultDTO payResult){
        log.debug("Received payment success notification: {}", payResult);
        orderService.handlePaySuccess(payResult);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "trade.refund.result.queue", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.PAY_EXCHANGE, type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.REFUND_CHANGE
    ))
    public void listenRefundResult(RefundResultDTO refundResult){
        log.debug("Received refund change success notification: {}", refundResult);
        refundApplyService.handleRefundResult(refundResult);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "trade.delay.order.query", durable = "true"),
            exchange = @Exchange(name = MqConstants.Exchange.TRADE_DELAY_EXCHANGE, delayed = "true", type = ExchangeTypes.TOPIC),
            key = MqConstants.Key.ORDER_DELAY_KEY
    ))
    public void listenOrderDelayQueryMessage(OrderDelayQueryDTO message){
        log.debug("Received order delay query notification: {}", message);
        payService.queryPayResult(message);
    }
}
