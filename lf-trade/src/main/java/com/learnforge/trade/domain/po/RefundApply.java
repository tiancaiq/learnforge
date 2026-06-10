package com.learnforge.trade.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Refund Application
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("refund_apply")
public class RefundApply implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Refund ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Order Detail ID
     */
    private Long orderDetailId;

    /**
     * Order id
     */
    private Long orderId;
    /**
     * Refund order number, unique identifier for each refund
     */
    private Long refundOrderNo;

    /**
     * Order user id
     */
    private Long userId;

    /**
     * Refund Amount
     */
    private Integer refundAmount;

    /**
     * Refund status, 1: Pending approval, 2: Cancel refund, 3: Approved refund, 4: Refuse refund, 5: Refund successful, 6: Refund failed
     */
    private Integer status;
    /**
     * Refund status description
     */
    private String message;
    /**
     * Reason for refund application
     */
    private String refundReason;
    /**
     * Refund reason description
     */
    private String questionDesc;

    /**
     * Approver id
     */
    private Long approver;
    /**
     * Approval Comment
     */
    private String approveOpinion;
    /**
     * Approval remark
     */
    private String remark;

    /**
     * Refund channel
     */
    private String refundChannel;
    /**
     * Reason for refund failure
     */
    private String failedReason;

    /**
     * Refund application creation time
     */
    private LocalDateTime createTime;

    /**
     * Approval time
     */
    private LocalDateTime approveTime;

    /**
     * Refund completion time (successful or failed)
     */
    private LocalDateTime finishTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;
    /**
     * Creator
     */

    private Long creater;

    /**
     * Updater
     */

    private Long updater;

}
