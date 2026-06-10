package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@ApiModel("Course Information")
@Data
public class CourseDTO {
    @ApiModelProperty("Course ID")
    private Long id;
    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("First-level Course Category ID")
    private Long categoryIdLv1;
    @ApiModelProperty("Second-level Course Category ID")
    private Long categoryIdLv2;
    @ApiModelProperty("Third-level Course Category ID")
    private Long categoryIdLv3;
    @ApiModelProperty("Course Cover")
    private String coverUrl;
    @ApiModelProperty("Creation Time")
    private LocalDateTime createTime;
    @ApiModelProperty("Update Time")
    private LocalDateTime updateTime;
    @ApiModelProperty("Price")
    private Integer price;
    @ApiModelProperty("Video Playback Duration")
    private Integer duration;
    @ApiModelProperty("Course Validity Days")
    private Integer validDuration;
    @ApiModelProperty("Is Free")
    private Boolean free;
    @ApiModelProperty("Release Time")
    private LocalDateTime publishTime;
    @ApiModelProperty("Chapter Count")
    private Integer sections;
    @ApiModelProperty("Course Status")
    private  Byte status;
    @ApiModelProperty("Teacher ID")
    private Long teacher;
    @ApiModelProperty("Course Type, 1: Live Course, 2: Recorded Course")
    private Integer courseType;
    @ApiModelProperty("Update Time")
    private Long updater;
    @ApiModelProperty("Course Progress Step, 1: Basic Info, 2: Chapters, 3: Course Video, 4: Course Questions, 5: Course Teacher")
    private Integer step;
    @ApiModelProperty(value = "Course Enrollment Count (Sales)", example = "3920")
    private Integer sold = 0;
    @ApiModelProperty(value = "Course Rating Score, 45 Represents 4.5 Stars", example = "35")
    private Integer score = 0;
    @ApiModelProperty("Is Course Disabled, 0: Disabled, 1: Enabled")
    private Integer enable;
}