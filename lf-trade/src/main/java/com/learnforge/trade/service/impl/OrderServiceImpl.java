package com.learnforge.trade.service.impl;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.client.course.CourseClient;
import com.learnforge.api.client.promotion.PromotionClient;
import com.learnforge.api.constants.CourseStatus;
import com.learnforge.api.dto.course.CourseSimpleInfoDTO;
import com.learnforge.api.dto.promotion.CouponDiscountDTO;
import com.learnforge.api.dto.promotion.OrderCourseDTO;
import com.learnforge.api.dto.trade.OrderBasicDTO;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.pay.sdk.dto.PayResultDTO;
import com.learnforge.trade.config.TradeProperties;
import com.learnforge.trade.constants.OrderStatus;
import com.learnforge.trade.constants.RefundStatus;
import com.learnforge.trade.constants.TradeErrorInfo;
import com.learnforge.trade.domain.dto.PlaceOrderDTO;
import com.learnforge.trade.domain.po.Order;
import com.learnforge.trade.domain.po.OrderDetail;
import com.learnforge.trade.domain.query.OrderPageQuery;
import com.learnforge.trade.domain.vo.*;
import com.learnforge.trade.mapper.OrderMapper;
import com.learnforge.trade.service.ICartService;
import com.learnforge.trade.service.IOrderDetailService;
import com.learnforge.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.learnforge.common.constants.ErrorInfo.Msg.OPERATE_FAILED;
import static com.learnforge.trade.constants.TradeErrorInfo.ORDER_ALREADY_FINISH;
import static com.learnforge.trade.constants.TradeErrorInfo.ORDER_NOT_EXISTS;

/**
 * <p>
 * Order Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    private final CourseClient courseClient;
    private final IOrderDetailService detailService;
    private final ICartService cartService;
    private final TradeProperties tradeProperties;
    private final RabbitMqHelper rabbitMqHelper;
    private final PromotionClient promotionClient;

    @Override
    @Transactional
    public PlaceOrderResultVO placeOrder(PlaceOrderDTO placeOrderDTO) {
        Long userId = UserContext.getUser();
        // 1. Query Course Fee Information, If Not Purchasable, Directly Throw Error
        List<CourseSimpleInfoDTO> courseInfos = getOnShelfCourse(placeOrderDTO.getCourseIds());
        // 2. Package Order Information
        Order order = new Order();
        // 2.1. Calculate Order Amount
        Integer totalAmount = courseInfos.stream()
                .map(CourseSimpleInfoDTO::getPrice).reduce(Integer::sum).orElse(0);
        // TODO 2.2. Calculate Discount Amount
        order.setDiscountAmount(0);
        Integer realAmount = totalAmount - order.getDiscountAmount();
        // 2.3. Package Other Information
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setRealAmount(realAmount);
        order.setStatus(OrderStatus.NO_PAY.getValue());
        order.setMessage(OrderStatus.NO_PAY.getProgressName());
        // 2.4. Order ID
        Long orderId = placeOrderDTO.getOrderId();
        order.setId(orderId);

        // 3. Package Order Details
        List<OrderDetail> orderDetails = new ArrayList<>(courseInfos.size());
        for (CourseSimpleInfoDTO courseInfo : courseInfos) {
            orderDetails.add(packageOrderDetail(courseInfo, order));
        }

        // 4. Write to Database
        saveOrderAndDetails(order, orderDetails);

        // 5. Delete Shopping Cart Data
        cartService.deleteCartByUserAndCourseIds(userId, placeOrderDTO.getCourseIds());

        // 6. Build Order Result
        return PlaceOrderResultVO.builder()
                .orderId(orderId)
                .payAmount(realAmount)
                .status(order.getStatus())
                .payOutTime(LocalDateTime.now().plusMinutes(tradeProperties.getPayOrderTTLMinutes()))
                .build();
    }

    private List<CourseSimpleInfoDTO> getOnShelfCourse(List<Long> courseIds) {
        // 1. Query Course
        List<CourseSimpleInfoDTO> courseInfos = courseClient.getSimpleInfoList(courseIds);
        LocalDateTime now = LocalDateTime.now();
        // 2. Check Status
        for (CourseSimpleInfoDTO courseInfo : courseInfos) {
            // 2.1. Check if Course is Listed
            if(!CourseStatus.SHELF.equalsValue(courseInfo.getStatus())){
                throw new BizIllegalException(TradeErrorInfo.COURSE_NOT_FOR_SALE);
            }
            // 2.2. Check if Course is Expired
            if(courseInfo.getPurchaseEndTime().isBefore(now)){
                throw new BizIllegalException(TradeErrorInfo.COURSE_EXPIRED);
            }
        }
        return courseInfos;
    }


    @Override
    @Transactional
    public PlaceOrderResultVO enrolledFreeCourse(Long courseId) {
        Long userId = UserContext.getUser();
        // 1. Query Course Information
        List<Long> cIds = CollUtils.singletonList(courseId);
        List<CourseSimpleInfoDTO> courseInfos = getOnShelfCourse(cIds);
        if (CollUtils.isEmpty(courseInfos)) {
            // Course Does Not Exist
            throw new BizIllegalException(TradeErrorInfo.COURSE_NOT_EXISTS);
        }
        CourseSimpleInfoDTO courseInfo = courseInfos.get(0);
        if(!courseInfo.getFree()){
            // Non-Free Course, Directly Throw Error
            throw new BizIllegalException(TradeErrorInfo.COURSE_NOT_FREE);
        }
        // 2. Create Order
        Order order = new Order();
        // 2.1. Basic Information
        order.setUserId(userId);
        order.setTotalAmount(0);
        order.setDiscountAmount(0);
        order.setRealAmount(0);
        order.setStatus(OrderStatus.ENROLLED.getValue());
        order.setFinishTime(LocalDateTime.now());
        order.setMessage(OrderStatus.ENROLLED.getProgressName());
        // 2.2. Order ID
        Long orderId = IdWorker.getId(order);
        order.setId(orderId);

        // 3. Order Details
        OrderDetail detail = packageOrderDetail(courseInfo, order);

        // 4. Write to Database
        saveOrderAndDetails(order, CollUtils.singletonList(detail));

        // 5. Send MQ Message to Notify Enrollment Success
        rabbitMqHelper.send(
                MqConstants.Exchange.ORDER_EXCHANGE,
                MqConstants.Key.ORDER_PAY_KEY,
                OrderBasicDTO.builder()
                        .orderId(orderId)
                        .userId(userId)
                        .courseIds(cIds)
                        .finishTime(order.getFinishTime())
                        .build()
        );
        // 6. Return VO
        return PlaceOrderResultVO.builder()
                .orderId(orderId)
                .payAmount(0)
                .status(order.getStatus())
                .build();
    }

    @Override
    public OrderConfirmVO prePlaceOrder(List<Long> courseIds) {
        // 1. Query Course Information
        List<CourseSimpleInfoDTO> courseInfos = courseClient.getSimpleInfoList(courseIds);
        if (CollUtils.isEmpty(courseInfos)) {
            throw new BizIllegalException(TradeErrorInfo.COURSE_NOT_EXISTS);
        }
        List<OrderCourseVO> courses = BeanUtils.copyList(courseInfos, OrderCourseVO.class);
        // 2. Calculate Total Price
        int total = courseInfos.stream().mapToInt(CourseSimpleInfoDTO::getPrice).sum();
        // TODO 3. Calculate Discount
        List<OrderCourseDTO> orderCourses = courseInfos.stream()
                        .map(ci ->new OrderCourseDTO().setId(ci.getId()).setCateId(ci.getThirdCateId()).setPrice(ci.getPrice()))
                .collect(Collectors.toList());
        List<CouponDiscountDTO> discountSolution = promotionClient.findDiscountSolution(orderCourses);
        int discountAmount = 0;
        // 4. Generate Order ID
        long orderId = IdWorker.getId();
        // 5. Organize Return
        OrderConfirmVO vo = new OrderConfirmVO();
        vo.setOrderId(orderId);
        vo.setTotalAmount(total);
        vo.setDiscounts(discountSolution);
        vo.setCourses(courses);
        return vo;
    }

    private OrderDetail packageOrderDetail(CourseSimpleInfoDTO courseInfo, Order order) {
        OrderDetail detail = new OrderDetail();
        detail.setUserId(order.getUserId());
        detail.setOrderId(order.getId());
        detail.setStatus(order.getStatus());
        detail.setCourseId(courseInfo.getId());
        detail.setPrice(courseInfo.getPrice());
        detail.setCoverUrl(courseInfo.getCoverUrl());
        detail.setName(courseInfo.getName());
        detail.setValidDuration(courseInfo.getValidDuration());
        detail.setDiscountAmount(0);// TODO Calculate Discount Amount
        detail.setRealPayAmount(courseInfo.getPrice() - detail.getDiscountAmount());
        return detail;
    }

    @Override
    @Transactional
    public void saveOrderAndDetails(Order order, List<OrderDetail> orderDetails) {
        // 4.1. Write Order
        boolean success = save(order);
        if (!success) {
            throw new DbException(TradeErrorInfo.PLACE_ORDER_FAILED);
        }
        // 4.2. Write Order Details
        if(orderDetails.size() == 1){
            success = detailService.save(orderDetails.get(0));
        }else {
            success = detailService.saveBatch(orderDetails);
        }
        if (!success) {
            throw new DbException(TradeErrorInfo.PLACE_ORDER_FAILED);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Long userId = UserContext.getUser();
        // 1. Query Order
        Order order = getById(orderId);
        if (order == null || !userId.equals(order.getUserId())) {
            throw new BadRequestException(ORDER_NOT_EXISTS);
        }
        // 2. Check if Order Status is Already Canceled, Idempotency Check
        if(OrderStatus.CLOSED.equalsValue(order.getStatus())){
           // Order Already Canceled, No Need for Repeated Operations
           return;
        }
        // 3. Check if Order is Unpaid, Only Unpaid Orders Can Be Canceled
        if(!OrderStatus.NO_PAY.equalsValue(order.getStatus())){
            throw new BizIllegalException(ORDER_ALREADY_FINISH);
        }
        // 4. Update Order Status to Canceled
        boolean success = lambdaUpdate()
                .set(Order::getStatus, OrderStatus.CLOSED.getValue())
                .set(Order::getMessage, "User Cancels Order")
                .set(Order::getCloseTime, LocalDateTime.now())
                .eq(Order::getStatus, OrderStatus.NO_PAY.getValue())
                .eq(Order::getId, orderId)
                .update();
        if (!success) {
            return;
        }
        // 5. Update Order Item Status
        detailService.updateStatusByOrderId(orderId, OrderStatus.CLOSED.getValue());
    }

    @Override
    public void deleteOrder(Long id) {
        // 1. Get Logged-in User
        Long userId = UserContext.getUser();
        // 2. Query Order
        Order order = getById(id);
        if (order == null) {
            return;
        }
        // 3. Check if Order Owner Matches Current Logged-in User
        if(!Objects.equals(userId,order.getUserId())){
            // Not Matched, Indicates It Is Not the Current User's Order, End
            throw new BadRequestException("Cannot Delete Others' Orders");
        }
        // 4. Delete Order
        boolean success = removeById(id);
        if (!success) {
            throw new DbException(OPERATE_FAILED);
        }
    }

    @Override
    public PageDTO<OrderPageVO> queryMyOrderPage(OrderPageQuery pageQuery) {
        Long userId = UserContext.getUser();
        // 1. Pagination and Sorting Conditions
        Page<Order> p = pageQuery.toMpPageDefaultSortByCreateTimeDesc();
        // 2. Pagination Query Order
        Integer status = pageQuery.getStatus();
        Page<Order> page = lambdaQuery()
                .eq(status != null, Order::getStatus, status)
                .eq(Order::getUserId, userId)
                .page(p);
        // 3. Data Judgment
        List<Order> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(p);
        }
        // 4. Query Order Detail Information
        List<Long> orderIds = records.stream().map(Order::getId).collect(Collectors.toList());
        // 4.1. Query Order Details by Order ID
        List<OrderDetail> details = detailService.queryByOrderIds(orderIds);
        // 4.2. Group Order Details, Key is Order ID, Value is All Details Under the Order
        Map<Long, List<OrderDetailVO>> detailMap = details.stream()
                .map(od -> BeanUtils.copyBean(od, OrderDetailVO.class))
                .collect(Collectors.groupingBy(OrderDetailVO::getOrderId));
        // 5. Convert VO
        List<OrderPageVO> list = new ArrayList<>(orderIds.size());
        for (Order record : records) {
            // 5.1. Convert Order
            OrderPageVO v = BeanUtils.toBean(record, OrderPageVO.class);
            list.add(v);
            // 5.2. Write to VO
            v.setDetails(detailMap.get(record.getId()));
            v.setStatusDesc(OrderStatus.desc(v.getStatus()));
        }
        return PageDTO.of(page, list);
    }

    @Override
    public OrderVO queryOrderById(Long id) {
        // 1. Query Order
        Order order = getById(id);
        if (order == null) {
            throw new BadRequestException(ORDER_NOT_EXISTS);
        }
        // 2. Query Order Details
        List<OrderDetail> details = detailService.queryByOrderId(id);
        // 3. Convert VO
        // 3.1. Order
        OrderVO vo = BeanUtils.toBean(order, OrderVO.class);
        // 3.2. Order Details
        List<OrderDetailVO> dvs = BeanUtils.copyList(details, OrderDetailVO.class, (d, v) -> v.setCanRefund(
                // Order has been paid, and refund is not in progress, mark as refundable status
                OrderStatus.canRefund(d.getStatus()) && !RefundStatus.inProgress(v.getRefundStatus())
        ));
        vo.setDetails(dvs);
        // 3.3. Order Progress
        vo.setProgressNodes(detailService.packageProgressNodes(order, null));
        return vo;
    }

    @Override
    public PlaceOrderResultVO queryOrderStatus(Long orderId) {
        // 1. Query Order
        Order order = getById(orderId);
        if (order == null) {
            throw new BizIllegalException(ORDER_NOT_EXISTS);
        }
        // 2. Calculate Timeout Time
        LocalDateTime outTime = null;
        if(OrderStatus.NO_PAY.equalsValue(order.getStatus())){
            outTime = order.getCreateTime().plusMinutes(tradeProperties.getPayOrderTTLMinutes());
        }
        // 3. Package Result
        return PlaceOrderResultVO.builder()
                .orderId(orderId)
                .payAmount(order.getRealAmount())
                .status(order.getStatus())
                .payOutTime(outTime)
                .build();
    }

    @Override
    @Transactional
    public void handlePaySuccess(PayResultDTO payResult) {
        // 1. Query Order
        Order order = getById(payResult.getBizOrderId());
        if (order == null) {
            return;
        }
        // 2. Update Order Status
        Order o = new Order();
        o.setId(order.getId());
        o.setStatus(OrderStatus.PAYED.getValue());
        o.setPayTime(payResult.getSuccessTime());
        o.setPayChannel(payResult.getPayChannel());
        o.setPayOrderNo(payResult.getPayOrderNo());
        o.setMessage("User Payment Successful");
        updateById(o);
        // 3. Update Order Item
        detailService.markDetailSuccessByOrderId(o.getId(), payResult.getPayChannel(), payResult.getSuccessTime());
        // 4. Query Course Information Included in Order
        List<Long> cIds = detailService.queryCourseIdsByOrderId(o.getId());
        // 5. Send MQ Message to Notify Enrollment Success
        rabbitMqHelper.send(
                MqConstants.Exchange.ORDER_EXCHANGE,
                MqConstants.Key.ORDER_PAY_KEY,
                OrderBasicDTO.builder()
                        .orderId(o.getId()).userId(order.getUserId()).courseIds(cIds)
                        .finishTime(o.getPayTime())
                        .build()
        );
    }

}
