package com.learnforge.promotion.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.learnforge.promotion.enums.ExchangeCodeStatus;
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
@TableName("exchange_code")
public class ExchangeCode implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    /**

     */
    @TableField("code")
    private String code;

    /**

     */
    @TableField("status")
    private ExchangeCodeStatus status;

    /**

     */
    @TableField("user_id")
    private Long userId;

    /**

     */
    @TableField("type")
    private Integer type;

    /**

     */
    @TableField("exchange_target_id")
    private Long exchangeTargetId;

    /**

     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**

     */
    @TableField("expired_time")
    private LocalDateTime expiredTime;

    /**

     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
