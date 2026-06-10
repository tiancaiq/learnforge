package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * CourseSimpleInfo
 * @author wusongsong
 * @since 2022/7/11 20:56
 * @version 1.0.0
 **/
@Data
@ApiModel("CourseSimpleInfo")
public class CourseSimpleInfoVO {
    @ApiModelProperty("Course ID")
    private Long id;
    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("Cover URL")
    private String coverUrl;
    @ApiModelProperty("Price")
    private Integer price;
    @ApiModelProperty("First-level Category ID")
    private Long firstCateId;
    @ApiModelProperty("Second-level Category ID")
    private Long secondCateId;
    @ApiModelProperty("Third-level Category ID")
    private Long thirdCateId;

    @ApiModelProperty("ChapterCount")
    private Integer sectionNum;
    @ApiModelProperty("CourseValidity")
    private Integer validDuration;
    @ApiModelProperty("CourseExpirationTime")
    private LocalDateTime purchaseEndTime;
}
