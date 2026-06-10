package com.learnforge.message.domain.dto;

import com.learnforge.common.domain.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * <p>
 * Announcement message template
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Notification task")
public class NoticeTaskDTO extends BaseDTO {

    @ApiModelProperty("Task id, not required when adding")
    private Long id;
    @ApiModelProperty("Task's notification template id to send")
    private Long templateId;
    @ApiModelProperty("Task name")
    private String name;
    @ApiModelProperty("true-Notify all;false-Notify some. Default false")
    private Boolean partial;
    @ApiModelProperty("Expected execution time of the task")
    private LocalDateTime pushTime;
    @ApiModelProperty("Maximum number of times the task can be repeated, 0 means no repetition")
    private Integer maxTimes;
    @ApiModelProperty("Interval between repeated executions, unit is minutes")
    private Long interval;
    @ApiModelProperty("Task expiration time")
    private LocalDateTime expireTime;
    @ApiModelProperty("Whether the task has already been completed. Default false")
    private Boolean finished;
}
