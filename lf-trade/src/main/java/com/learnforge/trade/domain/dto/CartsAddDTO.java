package com.learnforge.trade.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Course Added to Shopping Cart")
public class CartsAddDTO {
    @ApiModelProperty("Course ID to Add to Shopping Cart")
    @NotNull(message = "Course ID Cannot Be Empty")
    private Long courseId;
}