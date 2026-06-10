package com.learnforge.message.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Notification template
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notice_template")
public class NoticeTemplate implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Notification template id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Notification template name
     */
    private String name;

    /**
     * Notification template code, for example verify-code
     */
    private String code;

    /**
     * Notification type: 0-System notification, 1-Note notification, 2-Question and answer notification, 3-Other notification
     */
    private Integer type;

    /**
     * Template status: 0-draft, 1-in use, 2-disabled
     */
    private Integer status;

    /**
     * Notification title
     */
    private String title;

    /**
     * Notification content template
     */
    private String content;

    /**
     * Whether it includes third-party SMS template, default false
     */
    private Boolean isSmsTemplate;

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
