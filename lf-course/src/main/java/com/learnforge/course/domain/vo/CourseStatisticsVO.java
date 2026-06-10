package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * CourseDataStatistics
 * @author wusongsong
 * @since 2022/7/10 15:36
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "CourseStatistics")
public class CourseStatisticsVO {
    @ApiModelProperty("TotalCourseCount")
    private Integer totalNum;
    @ApiModelProperty("PublishedCourseCount")
    private Integer onSaleNum;
    @ApiModelProperty("OfflineCourseCount")
    private Integer offShelfNum;
    @ApiModelProperty("PendingPublishedCourseCount")
    private Integer noSaleNum;
    @ApiModelProperty("CompletedCourseCount")
    private Integer finishedNum;
    @ApiModelProperty("RecordedCourseCount")
    private Integer recordNum;
    @ApiModelProperty("LiveCourseCount")
    private Integer liveNum;

}
