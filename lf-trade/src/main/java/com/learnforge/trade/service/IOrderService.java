package com.learnforge.trade.service;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.pay.sdk.dto.PayResultDTO;
import com.learnforge.trade.domain.dto.PlaceOrderDTO;
import com.learnforge.trade.domain.po.Order;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.trade.domain.po.OrderDetail;
import com.learnforge.trade.domain.query.OrderPageQuery;
import com.learnforge.trade.domain.vo.OrderConfirmVO;
import com.learnforge.trade.domain.vo.OrderPageVO;
import com.learnforge.trade.domain.vo.OrderVO;
import com.learnforge.trade.domain.vo.PlaceOrderResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * Order service class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
public interface IOrderService extends IService<Order> {

    PlaceOrderResultVO placeOrder(PlaceOrderDTO placeOrderDTO);

    @Transactional
    void saveOrderAndDetails(Order order, List<OrderDetail> orderDetails);

    void cancelOrder(Long orderId);

    void deleteOrder(Long id);

    PageDTO<OrderPageVO> queryMyOrderPage(OrderPageQuery pageQuery);

    OrderVO queryOrderById(Long id);

    PlaceOrderResultVO queryOrderStatus(Long orderId);

    void handlePaySuccess(PayResultDTO payResult);

    PlaceOrderResultVO enrolledFreeCourse(Long courseId);

    OrderConfirmVO prePlaceOrder(List<Long> courseIds);

}
