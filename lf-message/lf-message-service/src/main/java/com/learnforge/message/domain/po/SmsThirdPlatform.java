package com.learnforge.message.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Third-party cloud communication platform
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sms_third_platform")
public class SmsThirdPlatform implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SMS platform id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * SMS platform name
     */
    private String name;

    /**
     * SMS platform code, for example: ali
     */
    private String code;

    /**
     * The smaller the number, the higher the priority, minimum is 0
     */
    private Integer priority;

    /**
     * SMS platform status: 0-Disabled, 1-Enabled
     */
    private Integer status;

    /**
     * Creator
     */

    private Long creater;

    /**
     * Updater
     */

    private Long updater;


    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;


}
