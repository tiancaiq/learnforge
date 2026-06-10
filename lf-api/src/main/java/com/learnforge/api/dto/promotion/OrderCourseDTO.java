package com.learnforge.api.dto.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel(description = "Order Course DTO")
public class OrderCourseDTO {
    @ApiModelProperty("Id")
    private Long id;
    @ApiModelProperty("Cate ID")
    private Long cateId;
    @ApiModelProperty("Price")
    private Integer price;
}
