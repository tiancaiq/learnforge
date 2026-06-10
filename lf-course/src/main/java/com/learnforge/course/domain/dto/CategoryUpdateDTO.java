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
 * Course Category Update Class, Only Update Name and Sort
 * @author wusongsong
 * @since 2022/7/10 15:32
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Category Info Update Model")
public class CategoryUpdateDTO {
    @ApiModelProperty("Classification id")
    @NotNull(message = CourseErrorInfo.Msg.CATEGORY_ID_NOT_NULL)
    private Long id;
    @ApiModelProperty("Name")
    @NotNull(message = CourseErrorInfo.Msg.CATEGORY_UPDATE_NAME_NOT_NULL)
    @Size(max = 15, message = CourseErrorInfo.Msg.CATEGORY_UPDATE_NAME_SIZE)
    private String name;
    @ApiModelProperty("Category Sort Number")
    @Max(value = 99, message = CourseErrorInfo.Msg.CATEGORY_UPDATE_INDEX_MAX_MIN)
    @Min(value = 1, message = CourseErrorInfo.Msg.CATEGORY_UPDATE_INDEX_MAX_MIN)
    @NotNull(message = CourseErrorInfo.Msg.CATEGORY_UPDATE_INDEX_NOT_NULL)
    private Integer index;
}
