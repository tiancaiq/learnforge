package com.learnforge.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Course Category Pagination Query
 * @author wusongsong
 * @since 2022/7/10 11:21
 * @version 1.0.0
 **/
@ApiModel(description = "Course Category Pagination Query Conditions")
@Data
public class CategoryListDTO {
    @ApiModelProperty("Category Status 1: Normal, 2: Disabled")
    private Integer status;
    @ApiModelProperty("Category Name")
    private String name;


}
