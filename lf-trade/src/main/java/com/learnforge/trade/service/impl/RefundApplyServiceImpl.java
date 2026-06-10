package com.learnforge.trade.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.learnforge.api.cache.RoleCache;
import com.learnforge.api.client.user.UserClient;
import com.learnforge.api.dto.trade.OrderBasicDTO;
import com.learnforge.api.dto.user.UserDTO;
import com.learnforge.common.autoconfigure.mq.RabbitMqHelper;
import com.learnforge.common.constants.Constant;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.constants.MqConstants;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.enums.UserType;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.exceptions.DbException;
import com.learnforge.common.utils.*;
import com.learnforge.pay.sdk.client.PayClient;
import com.learnforge.pay.sdk.constants.PayChannel;
import com.learnforge.pay.sdk.constants.RefundChannelEnum;
import com.learnforge.pay.sdk.dto.RefundApplyDTO;
import com.learnforge.pay.sdk.dto.RefundResultDTO;
import com.learnforge.trade.constants.OrderStatus;
import com.learnforge.trade.constants.RefundStatus;
import com.learnforge.trade.constants.TradeErrorInfo;
import com.learnforge.trade.domain.dto.ApproveFormDTO;
import com.learnforge.trade.domain.dto.RefundCancelDTO;
import com.learnforge.trade.domain.dto.RefundFormDTO;
import com.learnforge.trade.domain.po.Order;
import com.learnforge.trade.domain.po.OrderDetail;
import com.learnforge.trade.domain.po.RefundApply;
import com.learnforge.trade.domain.query.RefundApplyPageQuery;
import com.learnforge.trade.domain.vo.RefundApplyPageVO;
import com.learnforge.trade.domain.vo.RefundApplyVO;
import com.learnforge.trade.mapper.OrderMapper;
import com.learnforge.trade.mapper.RefundApplyMapper;
import com.learnforge.trade.service.IOrderDetailService;
import com.learnforge.trade.service.IRefundApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.learnforge.trade.constants.RefundStatus.AGREE;
import static com.learnforge.trade.constants.RefundStatus.REJECT;

/**
 * <p>
 * Refund Application Service Implementation Class
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
@Service
@RequiredArgsConstructor
public class RefundApplyServiceImpl extends ServiceImpl<RefundApplyMapper, RefundApply> implements IRefundApplyService {

    private final OrderMapper orderMapper;
    private final IOrderDetailService detailService;
    private final UserClient userClient;
    private final PayClient payClient;
    private final RoleCache roleCache;
    private final ThreadPoolTaskExecutor sendRefundRequestExecutor;
    private final RabbitMqHelper rabbitMqHelper;

    @Override
    public List<RefundApply> queryByDetailId(Long id) {
        // 1. Query in Reverse Order by ID, Latest Refund Application is at the Front
        List<RefundApply> list = baseMapper.queryByDetailId(id);
        // 2. Check for null
        if (CollUtils.isEmpty(list)) {
            return CollUtils.emptyList();
        }
        return list;
    }

    @Override
    @Transactional
    public void applyRefund(RefundFormDTO refundFormDTO) {
        Long userId = UserContext.getUser();
        // 1. Query order details
        OrderDetail detail = detailService.getBaseMapper().selectById(refundFormDTO.getOrderDetailId());
        if (detail == null) {
            throw new BadRequestException(TradeErrorInfo.ORDER_NOT_EXISTS);
        }
        if (detail.getRealPayAmount() == 0) {
            // Free Courses Cannot Be Refunded
            throw new BadRequestException(TradeErrorInfo.FREE_COURSE_CANNOT_REFUND);
        }
        // 2. Query Order
        Order order = orderMapper.getById(detail.getOrderId());
        if(order == null){
            throw new BadRequestException(TradeErrorInfo.ORDER_NOT_EXISTS);
        }
        if(!(OrderStatus.PAYED.equalsValue(order.getStatus()) || OrderStatus.REFUNDED.equalsValue(order.getStatus()))){
            // Order Status is Not Paid or Already Completed, Cannot Refund
            throw new BizIllegalException(TradeErrorInfo.ORDER_CANNOT_REFUND);
        }

        // 3. Query Applicant Information
        UserDTO userDTO = userClient.queryUserById(userId);
        AssertUtils.isNotNull(userDTO, ErrorInfo.Msg.USER_NOT_EXISTS);
        boolean isStudent = UserType.STUDENT.equalsValue(userDTO.getType());
        if (!userId.equals(detail.getUserId()) && isStudent) {
            // Applicant is Not the Order User and Not a Backend Administrator, Directly Report Error
            throw new BizIllegalException(TradeErrorInfo.NO_AUTH_REFUND);
        }

        // 4. Query the Number of Refund Applications Already Submitted
        List<RefundApply> refundApplies = queryByDetailId(refundFormDTO.getOrderDetailId());
        if (isStudent && refundApplies.size() >= 2) {
            throw new BizIllegalException(TradeErrorInfo.REFUND_TOO_MANY_TIMES);
        }
        // 5. Determine the Status of the Latest Refund, If Refund is in Progress, Return Directly
        if (CollUtils.isNotEmpty(refundApplies) && RefundStatus.inProgress(refundApplies.get(0).getStatus())) {
            throw new BizIllegalException(TradeErrorInfo.REFUND_IN_PROGRESS);
        }

        // 6. Submit Refund Application
        RefundApply refundApply = new RefundApply();
        refundApply.setOrderDetailId(detail.getId()); //Order Detail ID
        refundApply.setOrderId(detail.getOrderId()); //Order id
        refundApply.setUserId(detail.getUserId()); //Refund Order Owner
        refundApply.setRefundAmount(detail.getRealPayAmount()); //Refund Amount
        refundApply.setRefundReason(refundFormDTO.getRefundReason()); //Refund Reason
        refundApply.setQuestionDesc(refundFormDTO.getQuestionDesc()); //Refund Issue Description
        refundApply.setCreater(userId); //Application ID
        if (isStudent) {
            refundApply.setMessage("User Apply for Refund");
            refundApply.setStatus(RefundStatus.UN_APPROVE.getValue());
        } else {
            refundApply.setMessage("Administrator Direct Refund");
            refundApply.setStatus(AGREE.getValue());
        }
        boolean success = save(refundApply);
        if (!success) {
            // Refund Application Failed
            throw new DbException(ErrorInfo.Msg.DB_SAVE_EXCEPTION);
        }
        // 7. Update Order Status
        Order o = new Order();
        o.setId(refundApply.getOrderId());
        o.setStatus(OrderStatus.REFUNDED.getValue());
        o.setRefundTime(LocalDateTime.now());
        o.setMessage(isStudent ? "Student Apply for Refund" : "Administrator Direct Refund");
        int count = orderMapper.updateById(o);
        if (count < 1) {
            // Refund Application Failed
            throw new DbException(ErrorInfo.Msg.DB_SAVE_EXCEPTION);
        }
        // 8. Update Order Details Status
        OrderDetail d = new OrderDetail();
        d.setId(detail.getId());
        d.setStatus(OrderStatus.REFUNDED.getValue());
        d.setRefundStatus(refundApply.getStatus());
        detailService.updateById(d);
        // 9. If the Refund is Applied by an Administrator, Immediately Asynchronously Send Refund Request
        if(!isStudent) {
            sendRefundRequestAsync(refundApply);
        }
    }

    @Override
    public PageDTO<RefundApplyPageVO> queryRefundApplyByPage(RefundApplyPageQuery q) {
        // 1. Pagination and Sorting Conditions
        Page<RefundApply> p = searchRefundApply(q);

        // 2. Data processing
        List<RefundApply> records = p.getRecords();
        if (CollUtils.isEmpty(records)) {
            return PageDTO.empty(p);
        }
        // 3. Get User Information
        Map<Long, UserDTO> userMap = getRefundUserInfo(records);
        // 4. VO Conversion
        List<RefundApplyPageVO> list = new ArrayList<>(records.size());
        for (RefundApply r : records) {
            RefundApplyPageVO v = BeanUtils.copyBean(r, RefundApplyPageVO.class);
            list.add(v);
            // 4.1. Applicant
            UserDTO u = userMap.get(r.getCreater());
            v.setProposerName(roleCache.exchangeRoleName(u));
            v.setProposerMobile(u == null ? null : u.getCellPhone());
            // 4.2. Approver
            v.setApproverName(roleCache.exchangeRoleName(userMap.get(r.getApprover())));
            // 4.3. Refund Status
            v.setRefundStatusDesc(RefundStatus.desc(r.getStatus()));
            if (RefundStatus.SUCCESS.equalsValue(r.getStatus())) {
                v.setRefundSuccessTime(r.getFinishTime());
            }
        }
        return PageDTO.of(p, list);
    }

    private Page<RefundApply> searchRefundApply(RefundApplyPageQuery q) {
        Integer refundStatus = q.getRefundStatus();
        String defaultSortBy = "id";
        boolean isAsc = true;
        if (refundStatus != null) {
            if (RefundStatus.UN_APPROVE.equalsValue(refundStatus)) {
                defaultSortBy = Constant.DATA_FIELD_NAME_CREATE_TIME;
            } else {
                defaultSortBy = "approve_time";
                isAsc = false;
            }
        }
        Page<RefundApply> p = q.toMpPage(defaultSortBy, isAsc);

        // 2. Student Conditions
        Long userId = null;
        if (StringUtils.isNotBlank(q.getMobile())) {
            userId = userClient.exchangeUserIdWithPhone(q.getMobile());
            if (userId == null) {
                // Student does not exist, return empty data
                return Page.of(0, 0);
            }
        }

        // 3. Pagination Search
        p = lambdaQuery()
                .eq(q.getId() != null, RefundApply::getId, q.getId())
                .eq(refundStatus != null, RefundApply::getStatus, refundStatus)
                .eq(q.getOrderDetailId() != null, RefundApply::getOrderDetailId, q.getOrderDetailId())
                .eq(q.getOrderId() != null, RefundApply::getOrderId, q.getOrderId())
                .eq(userId != null, RefundApply::getUserId, userId)
                .ge(q.getApplyStartTime() != null, RefundApply::getCreateTime, q.getApplyStartTime())
                .le(q.getApplyEndTime() != null, RefundApply::getCreateTime, q.getApplyEndTime())
                .page(p);
        return p;
    }

    private Map<Long, UserDTO> getRefundUserInfo(List<RefundApply> records) {
        Set<Long> uIds = new HashSet<>();
        for (RefundApply record : records) {
            uIds.add(record.getCreater());
            uIds.add(record.getApprover());
        }
        uIds.remove(null);
        List<UserDTO> userDTOS = userClient.queryUserByIds(uIds);
        if (userDTOS.size() != uIds.size()) {
            throw new BizIllegalException("User Data is Incorrect");
        }
        return userDTOS.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));
    }

    @Override
    public RefundApplyVO queryRefundDetailById(Long id) {
        // 1. Query Refund Data
        RefundApply apply = getById(id);
        if (apply == null) {
            throw new BadRequestException(TradeErrorInfo.REFUND_NOT_EXISTS);
        }
        // 2. Convert VO
        RefundApplyVO vo = BeanUtils.copyBean(apply, RefundApplyVO.class);

        // 3. Query the order and transaction record
        Order order = orderMapper.getById(apply.getOrderId());
        if (order == null) {
            throw new BadRequestException(TradeErrorInfo.ORDER_NOT_EXISTS);
        }
        vo.setPayOrderNo(order.getPayOrderNo());
        vo.setPayChannel(PayChannel.desc(order.getPayChannel()));
        vo.setRefundChannel(RefundChannelEnum.desc(apply.getRefundChannel()));
        vo.setOrderTime(order.getCreateTime());
        vo.setPaySuccessTime(order.getPayTime());

        // 4. User Information
        Set<Long> uIds = new HashSet<>(2);
        uIds.add(apply.getUserId());
        uIds.add(apply.getCreater());
        // 4.1. Remote Query
        List<UserDTO> userDTOS = userClient.queryUserByIds(uIds);
        AssertUtils.isNotEmpty(userDTOS, TradeErrorInfo.COURSE_EXPIRED);
        Map<Long, UserDTO> userMap = userDTOS.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));
        // 4.2. Student
        UserDTO student = userMap.get(apply.getUserId());
        vo.setStudentName(roleCache.exchangeRoleName(student));
        vo.setMobile(student.getCellPhone());
        // 4.3. Applicant
        vo.setRefundProposerName(roleCache.exchangeRoleName(userMap.get(apply.getCreater())));

        // 6. Order Details
        OrderDetail detail = detailService.getBaseMapper().selectById(apply.getOrderDetailId());
        if (detail == null) {
            throw new BadRequestException(TradeErrorInfo.ORDER_NOT_EXISTS);
        }
        vo.setName(detail.getName());
        vo.setPrice(detail.getPrice());
        vo.setRealPayAmount(detail.getRealPayAmount());
        vo.setDiscountAmount(detail.getDiscountAmount());

        return vo;
    }

    @Override
    public RefundApplyVO nextRefundApplyToApprove() {
        // 1. Query a Pending Processing Application
        Long id = baseMapper.nextRefundApplyToApprove();
        // 2. Query Data and Return
        return queryRefundDetailById(id);
    }

    @Override
    @Transactional
    public void approveRefundApply(ApproveFormDTO approveDTO) {
        // 1. Query Application
        RefundApply apply = getById(approveDTO.getId());
        if (apply == null) {
            throw new BadRequestException(TradeErrorInfo.REFUND_NOT_EXISTS);
        }
        // 2. Check Status
        if (!RefundStatus.UN_APPROVE.equalsValue(apply.getStatus())) {
            // Approved Order, Cannot Be Approved Again
            throw new BadRequestException(TradeErrorInfo.REFUND_APPROVED);
        }
        // 3. Update Data
        boolean agree = approveDTO.getApproveType() == 1;
        RefundApply r = new RefundApply();
        r.setId(apply.getId());
        r.setApprover(UserContext.getUser());
        r.setStatus(agree ? AGREE.getValue() : REJECT.getValue());
        r.setApproveTime(LocalDateTime.now());
        r.setApproveOpinion(approveDTO.getApproveOpinion());
        r.setRemark(approveDTO.getRemark());
        r.setMessage(RefundStatus.desc(r.getStatus()));
        boolean success = updateById(r);

        if (!success) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }

        // 4. Update Sub-Order Status
        detailService.updateRefundStatusById(apply.getOrderDetailId(), r.getStatus());

        // 5. Asynchronously Send Refund Request
        if(agree) {
            sendRefundRequestAsync(apply);
        }
    }

    @Override
    @Transactional
    public void cancelRefundApply(RefundCancelDTO cancelDTO) {
        // 1. Query Refund Application Records
        Long applyId = cancelDTO.getId();
        Long detailId = cancelDTO.getOrderDetailId();
        List<RefundApply> list = lambdaQuery()
                .eq(applyId != null, RefundApply::getId, applyId)
                .eq(detailId != null, RefundApply::getOrderDetailId, detailId)
                .list();
        // 2. Check if empty
        if (CollUtils.isEmpty(list)) {
            return;
        }
        // 3. Get the Latest Refund Record, Determine Status
        RefundApply apply = list.get(0);
        if (!RefundStatus.UN_APPROVE.equalsValue(apply.getStatus())) {
            // Application has Already Been Approved or Refunded, Cannot Cancel
            throw new BizIllegalException(TradeErrorInfo.REFUND_APPROVED);
        }
        // 4. Update Refund Record
        RefundApply r = new RefundApply();
        r.setId(applyId);
        r.setStatus(RefundStatus.CANCEL.getValue());
        r.setMessage(RefundStatus.CANCEL.getProgressName());
        boolean success = updateById(r);
        if (!success) {
            throw new DbException(ErrorInfo.Msg.DB_UPDATE_EXCEPTION);
        }
        // 4. Update Sub-Order Status
        detailService.updateRefundStatusById(r.getOrderDetailId(), r.getStatus());
    }

    @Override
    public RefundApplyVO queryRefundDetailByDetailId(Long detailId) {
        // 1. Query Application Records
        List<RefundApply> refundApplies = queryByDetailId(detailId);
        if (CollUtils.isEmpty(refundApplies)) {
            return null;
        }

        // 2. Get the Latest Record, Convert to VO
        RefundApply apply = refundApplies.get(0);
        RefundApplyVO vo = BeanUtils.copyBean(apply, RefundApplyVO.class);

        // 3. Query Order Information
        Order order = orderMapper.getById(apply.getOrderId());
        vo.setPayOrderNo(order.getPayOrderNo());
        vo.setOrderTime(order.getCreateTime());
        vo.setPaySuccessTime(order.getPayTime());

        vo.setPayChannel(PayChannel.desc(order.getPayChannel()));
        vo.setRefundChannel(RefundChannelEnum.desc(apply.getRefundChannel()));
        vo.setRefundOrderNo(apply.getRefundOrderNo());
        return vo;
    }

    @Override
    @Transactional
    public void handleRefundResult(RefundResultDTO result) {
        // 1. Query Refund Application Records
        RefundApply refundApply = getById(result.getBizRefundOrderId());
        if (refundApply == null) {
            return;
        }
        UserContext.setUser(refundApply.getApprover());
        // 2. Determine Result, Alipay Payment May Directly Return Refund Success Result, WeChat Only Returns Refund in Progress
        RefundApply r = new RefundApply();
        r.setId(refundApply.getId());
        r.setRefundChannel(result.getRefundChannel());
        r.setRefundOrderNo(result.getRefundOrderNo());
        // 2.1. Determine if Refund is in Progress
        int status = result.getStatus();
        if(status == RefundResultDTO.RUNNING){
            // Refund in Progress, Result Unknown, Write Other Data to Database
            updateById(r);
            return;
        }

        // 2.2. Determine if Refund is Successful or Failed
        if(status == RefundResultDTO.SUCCESS){
            // Refund Successful, Record Status
            r.setStatus(RefundStatus.SUCCESS.getValue());
            r.setMessage(RefundStatus.SUCCESS.getProgressName());
        }else {
            // 2.3. Refund failed, need to record status and refund failure reason
            r.setStatus(RefundStatus.FAILED.getValue());
            r.setMessage(RefundStatus.FAILED.getProgressName());
            r.setFailedReason(result.getMsg());
        }

        // 2.4. Update database
        r.setFinishTime(LocalDateTime.now());
        updateById(r);

        // 3. Update sub-order status
        detailService.updateRefundStatusById(refundApply.getOrderDetailId(), r.getStatus());

        // 4. If refund is successful, need to cancel the user's course registration
        if (status == RefundResultDTO.SUCCESS) {
            // 4.1. Query sub-order information
            OrderDetail detail = detailService.getById(refundApply.getOrderDetailId());
            // 4.2. Send MQ message to notify registration success
            rabbitMqHelper.send(
                    MqConstants.Exchange.ORDER_EXCHANGE,
                    MqConstants.Key.ORDER_REFUND_KEY,
                    OrderBasicDTO.builder()
                            .orderId(refundApply.getOrderId())
                            .userId(refundApply.getUserId())
                            .courseIds(CollUtils.singletonList(detail.getCourseId())).build());
        }
    }

    @Override
    public List<RefundApply> queryApplyToSend(int index, int size) {
        Page<RefundApply> page = lambdaQuery()
                .eq(RefundApply::getStatus, AGREE.getValue())
                .page(new Page<>(index, size));
        if (page == null || CollUtils.isEmpty(page.getRecords())) {
            return CollUtils.emptyList();
        }
        return page.getRecords();
    }

    @Override
    @Transactional
    public void sendRefundRequest(RefundApply refundApply) {
        // 1. Organize request parameters
        RefundApplyDTO applyDTO = RefundApplyDTO.builder()
                .bizOrderNo(refundApply.getOrderId())
                .bizRefundOrderNo(refundApply.getId())
                .refundAmount(refundApply.getRefundAmount())
                .build();
        // 2. Send refund request
        RefundResultDTO result = payClient.applyRefund(applyDTO);

        // 3. Process refund result
        handleRefundResult(result);
    }

    @Override
    @Transactional
    public boolean checkRefundStatus(RefundApply refundApply) {
        // 1. First check if refund was already successful
        Integer status = refundApply.getStatus();
        if(!AGREE.equalsValue(status)){
            return true;
        }
        // 2. Remote query to determine if refund was already successful
        RefundResultDTO result = payClient.queryRefundResult(refundApply.getId());
        if (result == null) {
            // Refund data does not exist, abandon processing
            return false;
        }
        // 3. Process refund result
        handleRefundResult(result);
        return result.getStatus() != RefundResultDTO.RUNNING;
    }

    private void sendRefundRequestAsync(RefundApply refundApply) {
        sendRefundRequestExecutor.execute(() -> this.sendRefundRequest(refundApply));
    }
}
