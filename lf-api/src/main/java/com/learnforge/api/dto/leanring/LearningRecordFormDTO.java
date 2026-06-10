package com.learnforge.api.dto.leanring;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Learning record form data")
public class LearningRecordFormDTO {

    @ApiModelProperty("Section type: 1-video, 2-exam")
    private Integer sectionType;

    @ApiModelProperty("Schedule ID")
    private Long lessonId;

    @ApiModelProperty("Corresponding section id")
    private Long sectionId;

    @ApiModelProperty("Total video duration, unit: seconds")
    private Integer duration;

    @ApiModelProperty("Current playback duration of video, unit: seconds, fill 0 on first submission")
    private Integer moment;

    @ApiModelProperty("Submission time")
    private LocalDateTime commitTime;
}