package com.learnforge.pay.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learnforge.pay.third.model.RefundStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Refund order
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("refund_order")
public class RefundOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Business-side already paid order id
     */
    private Long bizOrderNo;

    /**
     * Business-side order id to be refunded
     */
    private Long bizRefundOrderNo;

    /**
     * Payment order number submitted to third-party
     */
    private Long payOrderNo;

    /**
     * Refund order number, unique identifier for each refund
     */
    private Long refundOrderNo;

    /**
     * Refund amount this time, unit is cents
     */
    private Integer refundAmount;

    /**
     * Total amount, unit is cents
     */
    private Integer totalAmount;

    /**
     * Is partial refund
     */
    private Boolean isSplit;

    /**
     * Payment channel id
     */
    private String payChannelCode;
    /**
     * Third-party transaction code
     */
    private String resultCode;

    /**
     * Third-party transaction information
     */
    private String resultMsg;

    /**
     * Refund status, 1: Refunding, 2: Refund succeeded, 3: Refund failed
     */
    private Integer status;

    /**
     * Refund channel
     */
    private String refundChannel;

    /**
     * Business-side refund notification failure count
     */
    private Integer notifyFailedTimes;

    /**
     * Refund interface notification status, 0: Pending notification, 1: Notification succeeded, 2: Notification in progress, 3: Notification failed
     */
    private Integer notifyStatus;

    /**
     * Refund document creation time
     */
    private LocalDateTime createTime;

    /**
     * Refund document modification time
     */
    private LocalDateTime updateTime;

    /**
     * Document creator, usually has value for manually reconciled documents
     */
    private Long creater;

    /**
     * Document modifier, usually has value for manually reconciled documents
     */
    private Long updater;

    /**
     * Logical Deletion
     */
    @TableLogic
    private Boolean deleted;


    public boolean success(){
        return RefundStatus.SUCCESS.equalsValue(status);
    }

    public boolean failed(){
        return RefundStatus.FAILED.equalsValue(status);
    }

    public boolean unknown(){
        return RefundStatus.UN_KNOWN.equalsValue(status);
    }

    public boolean notCommit(){
        return RefundStatus.NOT_COMMIT.equalsValue(status);
    }
}
