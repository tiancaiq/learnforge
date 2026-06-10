package com.learnforge.trade.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.trade.domain.dto.PlaceOrderDTO;
import com.learnforge.trade.domain.query.OrderPageQuery;
import com.learnforge.trade.domain.vo.OrderConfirmVO;
import com.learnforge.trade.domain.vo.OrderPageVO;
import com.learnforge.trade.domain.vo.OrderVO;
import com.learnforge.trade.domain.vo.PlaceOrderResultVO;
import com.learnforge.trade.service.IOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Order Frontend Controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
@Api(tags = "Order Related Interfaces")
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;

    @ApiOperation("Page Query My Orders")
    @GetMapping("page")
    public PageDTO<OrderPageVO> queryMyOrderPage(OrderPageQuery pageQuery){
        return orderService.queryMyOrderPage(pageQuery);
    }

    @ApiOperation("Query Order Details by ID")
    @GetMapping("/{id}")
    public OrderVO queryOrderById(@ApiParam ("Order id")@PathVariable("id") Long id){
        return orderService.queryOrderById(id);
    }

    @ApiOperation("Query Order Payment Status")
    @GetMapping("/{id}/status")
    public PlaceOrderResultVO queryOrderStatus(@ApiParam("Order id") @PathVariable("id") Long orderId) {
        return orderService.queryOrderStatus(orderId);
    }

    @ApiOperation("Pre-Order Interface, Generate Order ID, Confirm Order Coupon Information")
    @GetMapping("prePlaceOrder")
    public OrderConfirmVO prePlaceOrder(@RequestParam("courseIds")List<Long> courseIds) {
        return orderService.prePlaceOrder(courseIds);
    }

    @ApiOperation("Place Order Interface")
    @PostMapping("placeOrder")
    public PlaceOrderResultVO placeOrder(@RequestBody @Validated PlaceOrderDTO placeOrderDTO) {
        return orderService.placeOrder(placeOrderDTO);
    }

    @ApiOperation("Free Course Immediate Registration Interface")
    @PostMapping("/freeCourse/{courseId}")
    public PlaceOrderResultVO enrolledFreeCourse(@ApiParam("Free Course ID") @PathVariable("courseId") Long courseId) {
        return orderService.enrolledFreeCourse(courseId);
    }

    @ApiOperation("Cancel Order Interface")
    @PutMapping("/{id}/cancel")
    public void cancelOrder(@ApiParam("Order ID to Cancel") @PathVariable("id") Long orderId){
        orderService.cancelOrder(orderId);
    }

    @ApiOperation("Delete Order Interface")
    @DeleteMapping("/{id}")
    public void deleteOrder(@ApiParam("Order ID to Delete") @PathVariable("id") Long id) {
        orderService.deleteOrder(id);
    }
}
