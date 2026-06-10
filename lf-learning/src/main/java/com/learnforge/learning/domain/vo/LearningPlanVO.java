package com.learnforge.learning.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Learning Plan VO")
public class LearningPlanVO {

    @ApiModelProperty("Id")
    private Long id;

    @ApiModelProperty("Course ID")
    private Long courseId;

    @ApiModelProperty("Course Name")
    private String courseName;

    @ApiModelProperty("Week Freq")
    private Integer weekFreq;

    @ApiModelProperty("Sections")
    private Integer sections;

    @ApiModelProperty("Week Learned Sections")
    private Integer weekLearnedSections;

    @ApiModelProperty("Learned Sections")
    private Integer learnedSections;

    @ApiModelProperty("Latest Learn Time")
    private LocalDateTime latestLearnTime;
}
