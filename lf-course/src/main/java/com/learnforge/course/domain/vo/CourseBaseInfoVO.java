package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wusongsong
 * @since 2022/7/11 11:59
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Course Basic Info")
public class CourseBaseInfoVO {
    @ApiModelProperty("Course ID")
    private Long id;
    @ApiModelProperty("First-level Category ID")
    private Long firstCateId;
    @ApiModelProperty("Second-level Category ID")
    private Long secondCateId;
    @ApiModelProperty("Third-level Category ID")
    private Long thirdCateId;
    @ApiModelProperty("CourseCreator")
    private String createrName;
    private Long creater;
    @ApiModelProperty("Creation Time")
    private LocalDateTime createTime;
    @ApiModelProperty("Cover URL")
    private String coverUrl;
    @ApiModelProperty("Update Time")
    private LocalDateTime updateTime;
    @ApiModelProperty("UpdaterName")
    private String updaterName;
    private Long updater;
    @ApiModelProperty("TotalLessons, Excluding Chapters and Tests, Empty When Editing")
    private Integer cataTotalNum;
    @ApiModelProperty("CourseRating, Empty When Editing")
    private Double coureScore = 0d;
    @ApiModelProperty("CourseRating")
    private Integer score;
    @ApiModelProperty("EnrollmentCount, Empty When Editing")
    private Integer enrollNum = 0;
    @ApiModelProperty("LearningCount, Empty When Editing")
    private Integer studyNum = 0;
    @ApiModelProperty("RefundCount, Empty When Editing")
    private Integer refundNum = 0;
    @ApiModelProperty("TotalPaidAmount, Empty When Editing")
    private Integer realPayAmount = 0;
    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("CourseClassificationName, Separated by /")
    private String cateNames;
    @ApiModelProperty("Course Price")
    private Integer price;
    @ApiModelProperty("PurchaseValidityStart")
    private LocalDateTime purchaseStartTime;
    @ApiModelProperty
    private LocalDateTime purchaseEndTime;
    @ApiModelProperty("Validity period")
    private Integer validDuration;
    @ApiModelProperty("Course Description")
    private String introduce;
    @ApiModelProperty("Target Audience")
    private String usePeople;
    @ApiModelProperty("Details")
    private String detail;
    //
    @ApiModelProperty("IsModifiable, Default is Not Modifiable")
    private Boolean canUpdate = false;
    @ApiModelProperty("Is Free")
    private Boolean free;
    @ApiModelProperty("Step, 1: Basic Info Saved, 2: Course Directory Saved, 3: Course Video Saved, 4: Questions Saved, 5: Teacher Info Saved")
    private Integer step;
    @ApiModelProperty("Course Status, 1: Pending Upload, 2: Uploaded, 3: Offline, 4: Completed")
    private Integer status;

}
