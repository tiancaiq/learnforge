package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Course information in the order")
public class OrderCourseVO {
    @ApiModelProperty("Course ID")
    private Long id;
    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("Course cover url")
    private String coverUrl;
    @ApiModelProperty("Course price, unit: yuan")
    private Integer price;
}
