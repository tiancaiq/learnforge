package com.learnforge.auth.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * Login information record table
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("login_record")
public class LoginRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * User id
     */
    private Long userId;

    /**
     * User id
     */
    private String cellPhone;

    /**
     * Login time
     */
    private LocalDateTime loginTime;

    /**
     * Logout time
     */
    private LocalDateTime logoutTime;

    /**
     * Login date
     */
    private LocalDate loginDate;

    /**
     * Login duration, unit is seconds
     */
    private Long duration;

    /**
     * IP address
     */
    private String ipv4;


}
