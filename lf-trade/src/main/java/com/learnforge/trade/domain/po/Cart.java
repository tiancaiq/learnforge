package com.learnforge.trade.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Shopping cart item information, that is, the course in the shopping cart
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Shopping Cart Item ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * User id
     */
    private Long userId;

    /**
     * Course ID
     */
    private Long courseId;

    /**
     * Course cover path
     */
    private String coverUrl;

    /**
     * Course Name
     */
    private String courseName;

    /**
     * Unit price
     */
    private Integer price;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;


}
