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
 * Announcement message template
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("public_notice")
public class PublicNotice implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Announcement id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Announcement type
     */
    private Integer type;
    /**
     * Announcement title
     */
    private String title;

    /**
     * Announcement notification content, can store announcement message template
     */
    private String content;

    /**
     * Expected sending time of the announcement
     */
    private LocalDateTime pushTime;

    /**
     * Notification publish time
     */
    private LocalDateTime createTime;

    /**
     * Notification expiration time
     */
    private LocalDateTime expireTime;


}
