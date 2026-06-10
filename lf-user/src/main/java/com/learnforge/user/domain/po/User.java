package com.learnforge.user.domain.po;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learnforge.common.enums.UserType;
import com.learnforge.user.enums.UserStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Student user table
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId(value = "id")
    private Long id;

    /**
     * Username
     */
    private String username;

    /**
     * Phone number
     */
    private String cellPhone;

    /**
     * Password
     */
    private String password;

    /**
     * Account status: 0 - disabled, 1 - normal
     */
    private UserStatus status;

    /**
     * User type: 1 - other staff, 2 - regular student, 3 - teacher
     */
    private UserType type;

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
     * Modifier ID
     */

    private Long updater;
}
