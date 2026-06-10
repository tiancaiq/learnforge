package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @ClassName CategoryDTO
 * @author wusongsong
 * @since 2022/7/21 14:51
 * @version 1.0.0
 **/
@Data
@ApiModel("Course category")
public class CategoryDTO {
    @ApiModelProperty("Course category id")
    private Long id;
    @ApiModelProperty("Course category name")
    private String name;
    @ApiModelProperty("Third-level Category Count")
    private Integer thirdCategoryNum;
    @ApiModelProperty("Course Count")
    private Integer courseNum;
    @ApiModelProperty("Status: 1: Normal, 2: Disabled")
    private Integer status;
    @ApiModelProperty("Status Description")
    private String statusDesc;
    @ApiModelProperty("Creation Time")
    private LocalDateTime createTime;
    @ApiModelProperty("Update Time")
    private LocalDateTime updateTime;
    @ApiModelProperty("Sort Order")
    private Integer index;
    @ApiModelProperty("Parent ID")
    private Long parentId;
    @ApiModelProperty("Level")
    private Integer level;
}
