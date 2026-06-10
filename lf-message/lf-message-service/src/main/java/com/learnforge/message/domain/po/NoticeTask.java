package com.learnforge.message.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * System announcement task table, can be delayed or scheduled to send announcements
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("notice_task")
public class NoticeTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Announcement task id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Task corresponding notification template id
     */
    private Long templateId;

    /**
     * Task name
     */
    private String name;

    /**
     * true-Notify all;false-Notify some. Default false
     */
    private Boolean partial;

    /**
     * Expected execution time of the task
     */
    private LocalDateTime pushTime;

    /**
     * Maximum number of times the task can be repeated, 0 means no repetition
     */
    private Integer maxTimes;

    /**
     * Task delay execution interval, unit is minutes
     */
    private Integer interval;

    /**
     * Task expiration time
     */
    private LocalDateTime expireTime;

    /**
     * Whether the task has already been completed
     */
    private Boolean finished;

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
