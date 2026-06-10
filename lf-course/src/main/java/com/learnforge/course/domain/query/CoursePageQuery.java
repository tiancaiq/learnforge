package com.learnforge.course.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import com.learnforge.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Course Search Conditions")
public class CoursePageQuery extends PageQuery {

    @ApiModelProperty(value = "Search Keywords", example = "Redis")
    private String keyword;
    @ApiModelProperty(value = "Course Primary Category ID", example = "1")
    private Long firstCateId;
    @ApiModelProperty(value = "Course Secondary Category ID", example = "2")
    private Long secondCateId;
    @ApiModelProperty(value = "Course Tertiary Category ID", example = "3")
    private Long thirdCateId;
    @ApiModelProperty(value = "Sales Mode, true: Free, false: Paid", example = "true")
    private Boolean free;
    @ApiModelProperty(value = "Course Status, 1: Pending Listing, 2: Listed, 3: Downgraded, 4: Completed", example = "1", required = true)
    private Integer status;
    @ApiModelProperty(value = "Course Type, 1-Recorded, 2-Live", example = "1")
    private Integer courseType;
    @ApiModelProperty(value = "Start Time of Update Time Interval", example = "2022-7-18 19:52:36")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime beginTime;
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    @ApiModelProperty(value = "End Time of Update Time Interval", example = "2022-7-18 19:52:36")
    private LocalDateTime endTime;
}
