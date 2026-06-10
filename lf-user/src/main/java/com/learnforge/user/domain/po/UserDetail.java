package com.learnforge.user.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learnforge.common.enums.UserType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * Teacher details table
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_detail")
public class UserDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Associated user ID
     */
    @TableId(value = "id", type = IdType.NONE)
    private Long id;

    /**
     * User type: 1 - staff, 2 - regular student, 3 - teacher
     */
    private UserType type;

    /**
     * Name
     */
    private String name;

    /**
     * Gender: 0-male, 1-female
     */
    private Integer gender;

    /**
     * Avatar address
     */
    private String icon;

    /**
     * Email
     */
    private String email;

    /**
     * QQ number
     */
    private String qq;

    /**
     * Birthday
     */
    private LocalDate birthday;

    /**
     * Position
     */
    private String job;

    /**
     * Province
     */
    private String province;

    /**
     * City
     */
    private String city;

    /**
     * District
     */
    private String district;

    /**
     * Personal introduction
     */
    private String intro;

    /**
     * Image URL
     */
    private String photo;

    /**
     * Role id
     */
    private Long roleId;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;

    /**
     * Creator id
     */
    private Long creater;

    /**
     * Updater id
     */
    private Long updater;

    /**
     * Department id
     */
    private Long depId;

    @TableField(exist = false)
    private String cellPhone;
    @TableField(exist = false)
    private String username;
    @TableField(exist = false)
    private Integer status;
}
