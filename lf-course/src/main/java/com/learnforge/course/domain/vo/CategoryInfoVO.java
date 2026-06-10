package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wusongsong
 * @since 2022/7/10 15:06
 * @version 1.0.0
 **/
@Data
public class CategoryInfoVO {
    @ApiModelProperty("Course category id")
    private Long id;
    @ApiModelProperty("Course category name")
    private String name;
    @ApiModelProperty("Status: 1: Normal, 2: Disabled")
    private Integer status;
    @ApiModelProperty("Status Description")
    private String statusDesc;
    @ApiModelProperty("Creation Time")
    private LocalDateTime createTime;
    @ApiModelProperty("Update Time")
    private LocalDateTime updateTime;
    @ApiModelProperty("ClassificationLevel, 1: Primary Classification, 2: Secondary Classification, 3: Tertiary Classification")
    private Integer categoryLevel;
    @ApiModelProperty("PrimaryClassificationName")
    private String firstCategoryName;
    @ApiModelProperty("SecondaryClassificationName")
    private String secondCategoryName;
    @ApiModelProperty("Sort Order")
    private Integer index;
}
