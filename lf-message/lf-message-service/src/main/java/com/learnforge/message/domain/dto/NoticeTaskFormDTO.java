package com.learnforge.message.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * Announcement message template
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Data
@ApiModel(description = "Form entity of the notification task")
public class NoticeTaskFormDTO {

    @ApiModelProperty("Task id, not required when adding")
    private Long id;
    @ApiModelProperty("Task's notification template id to send")
    private Long templateId;
    @ApiModelProperty("Task name")
    private String name;
    @ApiModelProperty("true-Notify all;false-Notify some. Default false")
    private Boolean partial;
    @ApiModelProperty("Expected execution time of the task, if null or less than or equal to current time, execute immediately")
    private LocalDateTime pushTime;
    @ApiModelProperty("Maximum number of times the task can be repeated, 0 means no repetition")
    private Integer maxTimes;
    @ApiModelProperty("Interval between repeated executions, unit is minutes")
    private Long interval;
    @ApiModelProperty("Task expiration time")
    private LocalDateTime expireTime;
}
