package com.learnforge.trade.service.impl;

import cn.hutool.db.DbRuntimeException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.cache.RoleCache;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.IdAndNumDTO;
import com.learnforge.api.dto.course.CoursePurchaseInfoDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.utils.*;
import com.learnforge.pay.sdk.client.PayClient;
import com.learnforge.pay.sdk.constants.PayChannel;
import com.learnforge.pay.sdk.constants.RefundChannelEnum;
import com.learnforge.trade.constants.OrderStatus;
import com.learnforge.trade.constants.RefundStatus;
import com.learnforge.trade.domain.po.Order;
import com.learnforge.trade.domain.po.OrderDetail;
import com.learnforge.trade.domain.po.RefundApply;
import com.learnforge.trade.domain.query.OrderDetailPageQuery;
import com.learnforge.trade.domain.vo.OrderDetailAdminVO;
import com.learnforge.trade.domain.vo.OrderDetailPageVO;
import com.learnforge.trade.domain.vo.OrderProgressNodeVO;
import com.learnforge.trade.mapper.OrderDetailMapper;
import com.learnforge.trade.mapper.OrderMapper;
import com.learnforge.trade.mapper.RefundApplyMapper;
import com.learnforge.trade.service.IOrderDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.learnforge.trade.constants.OrderStatus.*;
import static com.learnforge.trade.constants.TradeErrorInfo.ORDER_NOT_EXISTS;

/**
 * <p>
 * Order detail service implementation class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements IOrderDetailService {

    private final UserClient userClient;

    private final OrderMapper orderMapper;

    private final PayClient payClient;

    private final RefundApplyMapper applyMapper;

    private final RoleCache roleCache;

    @Override
    @Transactional
    public void updateStatusByOrderId(Long orderId, Integer status) {
        boolean success = lambdaUpdate()
                .set(OrderDetail::getStatus, status)
                .eq(OrderDetail::getOrderId, orderId)
                .update();
        if (!success) {
            throw new DbRuntimeException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
    }

    @Override
    public List<OrderDetail> queryByOrderIds(List<Long> orderIds) {
        return lambdaQuery().in(OrderDetail::getOrderId, orderIds).list();
    }

    @Override
    public List<OrderDetail> queryByOrderId(Long orderId) {
        return lambdaQuery().eq(OrderDetail::getOrderId, orderId).list();
    }

    @Override
    public PageDTO<OrderDetailPageVO> queryDetailForPage(OrderDetailPageQuery query) {
        // 1. Pagination and Sorting Conditions
        Page<OrderDetail> p = query.toMpPageDefaultSortByCreateTimeDesc();
        // 2. May have user conditions
        Long userId = null;
        if (StringUtils.isNotBlank(query.getMobile())) {
            userId = userClient.exchangeUserIdWithPhone(query.getMobile());
            if (userId == null) {
                // Student does not exist, return empty data
                return PageDTO.empty(0L, 0L);
            }
        }
        // 3. Search
        Page<OrderDetail> page = lambdaQuery()
                .eq(query.getId() != null, OrderDetail::getId, query.getId())
                .eq(query.getStatus() != null, OrderDetail::getStatus, query.getStatus())
                .eq(query.getRefundStatus() != null, OrderDetail::getRefundStatus, query.getRefundStatus())
                .eq(StringUtils.isNotBlank(query.getPayChannel()), OrderDetail::getPayChannel, query.getPayChannel())
                .ge(query.getOrderStartTime() != null, OrderDetail::getCreateTime, query.getOrderStartTime())
                .le(query.getOrderEndTime() != null, OrderDetail::getCreateTime, query.getOrderEndTime())
                .eq(userId != null, OrderDetail::getUserId, userId)
                .page(p);
        // 4. Judge whether it is empty
        List<OrderDetail> records = page.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(page);
        }
        // 5. Query user information in the order
        Set<Long> uIds = records.stream().map(OrderDetail::getUserId).collect(Collectors.toSet());
        List<UserDTO> users = userClient.queryUserByIds(uIds);
        AssertUtils.isNotEmpty(users, ErrorInfo.Msg.USER_NOT_EXISTS);
        Map<Long, UserDTO> userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));

        // 6. Data conversion
        List<OrderDetailPageVO> list = new ArrayList<>(records.size());
        for (OrderDetail record : records) {
            // 6.1. Convert vo
            OrderDetailPageVO v = BeanUtils.toBean(record, OrderDetailPageVO.class);
            list.add(v);
            // 6.2. User information
            UserDTO u = userMap.get(record.getUserId());
            v.setName(roleCache.exchangeRoleName(u));
            v.setMobile(u == null ? null : u.getCellPhone());
            // 6.3. Others
            v.setPayChannel(PayChannel.desc(record.getPayChannel()));
            v.setStatusDesc(OrderStatus.desc(record.getStatus()));
            v.setRefundStatusDesc(RefundStatus.desc(record.getRefundStatus()));
        }

        return PageDTO.of(page, list);
    }

    @Override
    public OrderDetailAdminVO queryOrdersDetailProgress(Long id) {
        // 1. Query order details
        OrderDetail detail = getById(id);
        if (detail == null) {
            throw new BadRequestException(ORDER_NOT_EXISTS);
        }
        // 2. Query corresponding order
        Order order = orderMapper.getById(detail.getOrderId());
        if (order == null) {
            throw new BadRequestException(ORDER_NOT_EXISTS);
        }
        // 3. Query refund application
        List<RefundApply> refundApplyList = null;
        RefundApply refundApply = null;
        if (detail.getRefundStatus() != null && detail.getRefundStatus() != 0) {
            refundApplyList = applyMapper.queryByDetailId(detail.getId());
            refundApply = refundApplyList.get(0);
        }

        // 4. Query student and applicant information
        Set<Long> uIds = new HashSet<>(2);
        uIds.add(detail.getUserId());
        if (refundApply != null) {
            uIds.add(refundApply.getCreater());
        }
        List<UserDTO> userDTOS = userClient.queryUserByIds(uIds);
        AssertUtils.isNotEmpty(userDTOS, ErrorInfo.Msg.USER_NOT_EXISTS);
        Map<Long, UserDTO> userMap = userDTOS.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));

        // 5. Data processing
        OrderDetailAdminVO vo = BeanUtils.toBean(detail, OrderDetailAdminVO.class);
        vo.setMessage(order.getMessage());
        vo.setPayChannel(PayChannel.desc(order.getPayChannel()));
        // 5.1. Order Flow Information
        vo.setPayOrderNo(order.getPayOrderNo());
        if (refundApply != null) {
            vo.setRefundOrderNo(refundApply.getRefundOrderNo());
            vo.setRefundChannel(RefundChannelEnum.desc(refundApply.getRefundChannel()));
            vo.setFailedReason(refundApply.getFailedReason());
        }
        // 5.2. User Information
        UserDTO student = userMap.get(detail.getUserId());
        vo.setStudentName(student.getName());
        vo.setMobile(student.getCellPhone());

        // 5.3. Refund Order Information
        if (refundApply != null) {
            vo.setRefundApplyId(refundApply.getId());
            vo.setRefundProposerName(userMap.get(refundApply.getCreater()).getName());
            vo.setRefundReason(refundApply.getRefundReason());
            vo.setRemark(refundApply.getRemark());
            vo.setRefundMessage(refundApply.getMessage());
        }

        // 5.4. Course Validity Period
        Integer validDuration = detail.getValidDuration();
        if (order.getPayTime() != null && validDuration != null && validDuration > 0) {
            // Already Paid Successfully, Set Course Validity Period
            vo.setStudyValidTime(order.getPayTime().plusMonths(validDuration));
        }

        // 5.5. Set Order Status
        List<OrderProgressNodeVO> progressNodes = packageProgressNodes(order, refundApply);
        vo.setNodes(progressNodes);

        // 5.6. Allow Refund:
        // - Already Paid and No Refund Has Been Initiated
        // - Only One Refund Has Been Initiated and Refund Process Has Been Completed
        vo.setCanRefund(
                (OrderStatus.canRefund(detail.getStatus()) && refundApply == null) ||
                        (refundApply != null && refundApplyList.size() == 1 &&
                                !RefundStatus.inProgress(refundApply.getStatus()))
        );
        return vo;
    }

    @Override
    public List<OrderProgressNodeVO> packageProgressNodes(Order order, RefundApply refundApply) {
        // 1. Fill in Time Values for Each Node of the Order Transaction
        List<OrderProgressNodeVO> list = new ArrayList<>();
        // 1.1. Order Creation Time
        list.add(new OrderProgressNodeVO(OrderStatus.NO_PAY.getProgressName(), order.getCreateTime()));
        // 1.2. Payment Success Time
        list.add(new OrderProgressNodeVO(PAYED.getProgressName(), order.getPayTime()));
        // 1.3. Transaction Closure Time
        list.add(new OrderProgressNodeVO(OrderStatus.CLOSED.getProgressName(), order.getCloseTime()));
        // 1.4. Transaction Completion Time
        list.add(new OrderProgressNodeVO(FINISHED.getProgressName(), order.getFinishTime()));
        if (refundApply == null) {
            // 1.5. In the Absence of Refund Parameters, Default is User-side Query, Add Refund Success Time Field
            list.add(new OrderProgressNodeVO(OrderStatus.REFUNDED.getProgressName(), order.getRefundTime()));
        } else {
            // 2. Fill in Time Values for Each Node of the Order Refund
            // 2.1. Order Refund Application
            list.add(new OrderProgressNodeVO(RefundStatus.UN_APPROVE.getProgressName(), refundApply.getCreateTime()));
            // 2.2. Order Approval Time (Approval Success or Failure)
            list.add(new OrderProgressNodeVO(RefundStatus.AGREE.getProgressName(), refundApply.getApproveTime()));
            // 2.3. Refund Completion Time (Student Cancellation, Refund Success, Refund Closure)
            RefundStatus status = RefundStatus.of(refundApply.getStatus());
            list.add(new OrderProgressNodeVO(status.getProgressName(), refundApply.getFinishTime()));
        }

        // 3. Filter Out Nodes Without Time, Then Sort by Time in Ascending Order
        return list.stream()
                .filter(nodeVO -> nodeVO.getTime() != null)
                .sorted(Comparator.comparing(OrderProgressNodeVO::getTime))
                .collect(Collectors.toList());
    }

    @Override
    public void markDetailSuccessByOrderId(Long id, String payChannel, LocalDateTime successTime) {
        List<OrderDetail> details = queryByOrderId(id);
        for (OrderDetail detail : details) {
            detail.setStatus(PAYED.getValue());
            detail.setPayChannel(payChannel);
            detail.setCourseExpireTime(successTime.plusMinutes(detail.getValidDuration()));
        }
        updateBatchById(details);
    }

    @Override
    public void updateRefundStatusById(Long orderDetailId, int status) {
        lambdaUpdate()
                .set(OrderDetail::getRefundStatus, status)
                .eq(OrderDetail::getId, orderDetailId)
                .update();
    }

    @Override
    public List<Long> queryCourseIdsByOrderId(Long orderId) {
        return baseMapper.queryCourseIdsByOrderId(orderId);
    }

    @Override
    public Boolean checkCourseOrderInfo(Long courseId) {
        // 1. Get user
        Long userId = UserContext.getUser();

        // 2. Query Order
        List<OrderDetail> orders = lambdaQuery()
                .eq(OrderDetail::getUserId, userId)
                .eq(OrderDetail::getCourseId, courseId)
                .in(OrderDetail::getStatus, PAYED.getValue(), FINISHED.getValue(), ENROLLED.getValue())
                .list();

        // 3. Check if Order Exists
        if (CollUtils.isEmpty(orders)) {
            return false;
        }

        // 4. Find Non-Expired Ones
        LocalDateTime now = LocalDateTime.now();
        return orders.stream().anyMatch(o -> o.getCourseExpireTime().isAfter(now));
    }

    @Override
    public Map<Long, Integer> countEnrollNumOfCourse(List<Long> courseIdList) {
        // 1. Condition Construction
        QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .in(OrderDetail::getCourseId, courseIdList)
                .in(OrderDetail::getStatus, PAYED.getValue(), FINISHED.getValue(), ENROLLED.getValue());

        // 2. Statistics
        List<IdAndNumDTO> list = baseMapper.countEnrollNumOfCourse(wrapper);

        // 3. Conversion and Return
        return IdAndNumDTO.toMap(list);
    }

    @Override
    public Map<Long, Integer> countEnrollCourseOfStudent(List<Long> studentIds) {
        // 1. Condition Construction
        QueryWrapper<OrderDetail> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .in(OrderDetail::getUserId, studentIds)
                .in(OrderDetail::getStatus, PAYED.getValue(), FINISHED.getValue(), ENROLLED.getValue());
        // 2. Statistics
        List<IdAndNumDTO> list = baseMapper.countEnrollCourseOfStudent(wrapper);

        // 3. Conversion and Return
        return IdAndNumDTO.toMap(list);
    }

    @Override
    public CoursePurchaseInfoDTO getPurchaseInfoOfCourse(Long courseId) {
        // 1. Count Enrollments
        Integer enrollNum = lambdaQuery()
                .eq(OrderDetail::getCourseId, courseId)
                .in(OrderDetail::getStatus, PAYED.getValue(), FINISHED.getValue(), ENROLLED.getValue())
                .count();
        // 2. Count Refunds
        Integer refundNum = lambdaQuery()
                .eq(OrderDetail::getCourseId, courseId)
                .eq(OrderDetail::getStatus, REFUNDED.getValue())
                .count();
        // 3. Count Sales Revenue
        int realPayAmount = baseMapper.countRealPayAmountByCourseId(courseId);

        return new CoursePurchaseInfoDTO(enrollNum, refundNum, realPayAmount);
    }
}
