package com.learnforge.trade.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * Order
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "`order`", autoResultMap = true)
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Order id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    /**
     * Payment transaction serial number
     */
    private Long payOrderNo;
    /**
     * User id
     */
    private Long userId;

    /**
     * Order status, 1: Pending payment, 2: Paid, 3: Closed, 4: Completed, 5: Registered, 6: Refund applied
     */
    private Integer status;

    /**
     * Status remark
     */
    private String message;

    /**
     * Order total amount, unit: cents
     */
    private Integer totalAmount;

    /**
     * Actual paid amount, unit: cents
     */
    private Integer realAmount;

    /**
     * Discount amount, unit: cents
     */
    private Integer discountAmount;

    /**
     * Payment channel
     */
    private String payChannel;

    /**
     * Coupon id
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> couponIds;

    /**
     * Order creation time
     */
    private LocalDateTime createTime;

    /**
     * Payment time
     */
    private LocalDateTime payTime;

    /**
     * Order closure time
     */
    private LocalDateTime closeTime;

    /**
     * Order completion time, 30 days after payment
     */
    private LocalDateTime finishTime;

    /**
     * Refund application time
     */
    private LocalDateTime refundTime;

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

    /**
     * Logical Deletion
     */
    @TableLogic
    private Integer deleted;


}
