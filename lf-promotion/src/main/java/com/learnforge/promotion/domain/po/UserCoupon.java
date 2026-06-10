package com.learnforge.promotion.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.learnforge.promotion.enums.UserCouponStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_coupon")
public class UserCoupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**

     */
    @TableField("user_id")
    private Long userId;

    /**

     */
    @TableField("coupon_id")
    private Long couponId;

    /**

     */
    @TableField("term_begin_time")
    private LocalDateTime termBeginTime;

    /**

     */
    @TableField("term_end_time")
    private LocalDateTime termEndTime;

    /**

     */
    @TableField("used_time")
    private LocalDateTime usedTime;

    /**

     */
    @TableField("status")
    private UserCouponStatus status;

    /**

     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**

     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
