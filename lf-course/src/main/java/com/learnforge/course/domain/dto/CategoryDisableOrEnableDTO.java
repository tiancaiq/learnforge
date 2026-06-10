package com.learnforge.course.domain.dto;

import com.learnforge.common.validate.annotations.EnumValid;
import com.learnforge.course.constants.CourseErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Course Directory Enable/Disable Model
 *
 * @author wusongsong
 * @since 2022/7/10 15:24
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Course Category Enable/Disable")
public class CategoryDisableOrEnableDTO {
    @ApiModelProperty("Course category id")
    @NotNull(message = CourseErrorInfo.Msg.CATEGORY_ID_NOT_NULL)
    private Long id;
    @ApiModelProperty("Course Category Status, 1: Enabled, 0: Disabled")
    @EnumValid(enumeration = {0,1}, message = CourseErrorInfo.Msg.CATEGORY_DISABLE_ENABLE_STATUS_ENUM)
    private Integer status;
}
