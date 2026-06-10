package com.learnforge.promotion.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.learnforge.promotion.enums.CouponStatus;
import com.learnforge.promotion.enums.DiscountType;
import com.learnforge.promotion.enums.ObtainType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**

     */
    @TableField("`name`")
    private String name;

    /**

     */
    @TableField("type")
    private Integer type;

    /**

     */
    @TableField("discount_type")
    private DiscountType discountType;

    /**

     */
    @TableField("`specific`")
    private Boolean specific;

    /**

     */
    @TableField("discount_value")
    private Integer discountValue;

    /**

     */
    @TableField("threshold_amount")
    private Integer thresholdAmount;

    /**

     */
    @TableField("max_discount_amount")
    private Integer maxDiscountAmount;

    /**

     */
    @TableField("obtain_way")
    private ObtainType obtainWay;

    /**

     */
    @TableField("issue_begin_time")
    private LocalDateTime issueBeginTime;

    /**

     */
    @TableField("issue_end_time")
    private LocalDateTime issueEndTime;

    /**

     */
    @TableField("term_days")
    private Integer termDays;

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
    @TableField("status")
    private CouponStatus status;

    /**

     */
    @TableField("total_num")
    private Integer totalNum;

    /**

     */
    @TableField("issue_num")
    private Integer issueNum;

    /**

     */
    @TableField("used_num")
    private Integer usedNum;

    /**

     */
    @TableField("user_limit")
    private Integer userLimit;

    /**

     */
    @TableField("ext_param")
    private String extParam;

    /**

     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**

     */
    @TableField("update_time")
    private LocalDateTime updateTime;

    /**

     */
    @TableField("creater")
    private Long creater;

    /**

     */
    @TableField("updater")
    private Long updater;


}
