package com.learnforge.course.domain.dto;

import com.learnforge.course.constants.CourseErrorInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Course Category New Model
 *
 * @author wusongsong
 * @since 2022/7/10 12:10
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Course Category New Model")
public class CategoryAddDTO {
    @ApiModelProperty("Parent Category ID, If New Primary Category parentId Pass 0")
    private Long parentId;

    @ApiModelProperty(value = "Name",required = true)
    @NotNull(message = CourseErrorInfo.Msg.CATEGORY_ADD_NAME_NOT_NULL)
    @Size(max = 15, message = CourseErrorInfo.Msg.CATEGORY_ADD_NAME_SIZE)
    private String name;

    @ApiModelProperty(value = "Category Sort Number",required = true)
    @Max(value = 99, message = CourseErrorInfo.Msg.CATEGORY_ADD_INDEX_MAX_MIN)
    @Min(value = 1, message = CourseErrorInfo.Msg.CATEGORY_ADD_INDEX_MAX_MIN)
    @NotNull(message = CourseErrorInfo.Msg.CATEGORY_ADD_INDEX_NOT_NULL)
    private Integer index;

}
