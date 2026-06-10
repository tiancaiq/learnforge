package com.learnforge.course.domain.dto;

import com.learnforge.course.constants.CourseErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author wusongsong
 * @since 2022/7/20 16:50
 * @version 1.0.0
 **/
@ApiModel(description = "Course ID")
@Data
public class CourseIdDTO {
    @ApiModelProperty("Course ID")
    @NotNull(message = CourseErrorInfo.Msg.COURSE_OPERATE_ID_NULL)
    private Long id;
}
