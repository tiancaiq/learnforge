package com.learnforge.message.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Third-party SMS platform signature and template information
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("message_template")
public class MessageTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * SMS sending template id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Template name
     */
    private String name;

    /**
     * Third-party SMS push channel id
     */
    private String platformCode;

    /**
     * Signature
     */
    private String signName;

    /**
     * Third-party SMS template code
     */
    private String thirdTemplateCode;

    /**
     * Third-party SMS template content preview
     */
    private String content;

    /**
     * Notification template id, system announcement-related SMS will be associated with this id
     */
    private Long templateId;

    /**
     * SMS template status, 0-Disabled, 1-Enabled
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
