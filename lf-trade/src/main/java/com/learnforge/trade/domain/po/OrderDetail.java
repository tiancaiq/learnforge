package com.learnforge.trade.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Order details
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("order_detail")
public class OrderDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Order Detail ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Order id
     */
    private Long orderId;

    /**
     * User id
     */
    private Long userId;

    /**
     * Course ID
     */
    private Long courseId;

    /**
     * Course Price
     */
    private Integer price;

    /**
     * Course Name
     */
    private String name;

    /**
     * Cover address
     */
    private String coverUrl;

    /**
     * Course learning validity period, unit: month. Calculated from payment time
     */
    private Integer validDuration;

    /**
     * Course learning expiration time
     */
    private LocalDateTime courseExpireTime;

    /**
     * Discount amount
     */
    private Integer discountAmount;

    /**
     * Actual paid amount
     */
    private Integer realPayAmount;

    /**
     * Order details status, 1: Pending payment, 2: Paid, 3: Closed, 4: Completed, 5: Registered
     */
    private Integer status;

    /**
     * 1: Pending approval, 2: Cancel refund, 3: Approved refund, 4: Refuse refund, 5: Refund successful, 6: Refund failed
     */
    private Integer refundStatus;

    /**
     * Payment channel name
     */
    private String payChannel;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

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
