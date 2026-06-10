package com.learnforge.learning.domain.query;

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
@ApiModel(description = "Question Admin Page Query")
public class QuestionAdminPageQuery extends PageQuery {
    @ApiModelProperty("Course Name")
    private String courseName;
    @ApiModelProperty("Status")
    private Integer status;
    @ApiModelProperty("Begin Time")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime beginTime;
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    @ApiModelProperty("End Time")
    private LocalDateTime endTime;
}
