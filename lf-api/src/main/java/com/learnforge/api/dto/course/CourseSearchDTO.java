package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Course Information
 * @ClassName CourseDTO
 * @author wusongsong
 * @since 2022/7/18 13:12
 * @version 1.0.0
 **/
@ApiModel(description = "Course Information")
@Data
public class CourseSearchDTO {
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
    @ApiModelProperty("Price")
    private Integer price;
    @ApiModelProperty("Is Free")
    private Boolean free;
    @ApiModelProperty("Release Time")
    private LocalDateTime publishTime;
    @ApiModelProperty("Chapter Count")
    private Integer sections;
    @ApiModelProperty("Course Duration")
    private Integer duration;
    @ApiModelProperty("Teacher ID")
    private Long teacher;
    @ApiModelProperty("Course Type, 1: Live Course, 2: Recorded Course")
    private Integer courseType;
    @ApiModelProperty(value = "Course Enrollment Count (Sales)", example = "3920")
    private Integer sold = 0;
    @ApiModelProperty(value = "Course Rating Score, 45 Represents 4.5 Stars", example = "35")
    private Integer score = 0;
}
