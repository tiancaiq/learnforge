package com.learnforge.learning.domain.vo;

import com.learnforge.learning.domain.enums.LessonStatus;
import com.learnforge.learning.domain.enums.PlanStatus;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Learning Lesson VO")
public class LearningLessonVO {

    @ApiModelProperty("Id")
    private Long id;

    @ApiModelProperty("Course ID")
    private Long courseId;

    @ApiModelProperty("Course Name")
    private String courseName;

    @ApiModelProperty("Course Cover Url")
    private String courseCoverUrl;

    @ApiModelProperty("Sections")
    private Integer sections;

    @ApiModelProperty("Status")
    private LessonStatus status;

    @ApiModelProperty("Learned Sections")
    private Integer learnedSections;

    @ApiModelProperty("Course Amount")
    private Integer courseAmount;

    @ApiModelProperty("Create Time")
    private LocalDateTime createTime;

    @ApiModelProperty("Expire Time")
    private LocalDateTime expireTime;

    @ApiModelProperty("Plan Status")
    private PlanStatus planStatus;

    @ApiModelProperty("Week Freq")
    private Integer weekFreq;

    @ApiModelProperty("Latest Section Name")
    private String latestSectionName;

    @ApiModelProperty("Latest Section Index")
    private Integer latestSectionIndex;
}
