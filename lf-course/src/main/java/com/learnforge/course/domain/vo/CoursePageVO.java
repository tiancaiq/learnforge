package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Course Information")
public class CoursePageVO {
    @ApiModelProperty(value = "Course ID", example = "1")
    private Long id;
    @ApiModelProperty(value = "Course Name", example = "Java")
    private String name;
    @ApiModelProperty(value = "CoursePrice, Unit in Fen", example = "32900")
    private Long price;
    @ApiModelProperty(value = "CourseCoverUrl", example = "default-cover-url.jpg")
    private String coverUrl;
    @ApiModelProperty(value = "CourseClassification, Tertiary Classification, Separated by /")
    private String categories;
    @ApiModelProperty(value = "CourseChapterCount", example = "25")
    private Integer sections;
    @ApiModelProperty(value = "Course Enrollment Count (Sales)", example = "3920")
    private Integer sold;
    @ApiModelProperty(value = "Course Rating Score, 45 Represents 4.5 Stars", example = "35")
    private Integer score;
    @ApiModelProperty(value = "Course Status, 1: Pending Listing, 2: Listed, 3: Downgraded, 4: Completed", example = "1")
    private Integer status;
    @ApiModelProperty(value = "UpdaterName", example = "32900")
    private String updaterName;
    @ApiModelProperty(value = "Update Time", example = "2022-7-18 19:52:36")
    private LocalDateTime updateTime;
    @ApiModelProperty("CourseEditProgress: 1: Basic Info Saved, 2: Course Directory Saved, 3: Course Video Saved, 4: Questions Saved, 5: Teacher Info Saved")
    private Integer step;
    @ApiModelProperty("CoursePublishTime")
    private LocalDateTime publishTime;
    @ApiModelProperty("OfflineTime")
    private LocalDateTime purchaseEndTime;

    public static final String[] EXCLUDE_FIELDS =
            {"free", "type", "teacher","duration","publishTime"};
}
