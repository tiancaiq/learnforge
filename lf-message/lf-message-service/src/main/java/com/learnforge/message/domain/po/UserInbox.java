package com.learnforge.message.domain.po;

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
 * User notification record
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("user_inbox")
public class UserInbox implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * User notification id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * User id
     */
    private Long userId;

    /**
     * Notification type: 0-System notification, 1-Note notification, 2-Question and answer notification, 3-Other notification
     */
    private Integer type;

    /**
     * Notification title
     */
    private String title;

    /**
     * Notification or private message content
     */
    private String content;

    /**
     * Whether the announcement has been read
     */
    private Boolean isRead;

    /**
     * Sender id of the notification, 0 means it is the system
     */
    private Long publisher;

    /**
     * Message push time
     */
    private LocalDateTime pushTime;

    /**
     * Expiration time, once expired, it will not be displayed on the user end
     */
    private LocalDateTime expireTime;


}
