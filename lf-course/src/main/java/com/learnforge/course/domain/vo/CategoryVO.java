package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wusongsong
 * @since 2022/7/10 11:32
 * @version 1.0.0
 **/
@ApiModel(description = "CourseClassificationInfo")
@Data
public class CategoryVO {
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
    @ApiModelProperty("SubClassificationList")
    private List<CategoryVO> children;
}
