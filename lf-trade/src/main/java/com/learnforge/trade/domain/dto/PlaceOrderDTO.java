package com.learnforge.trade.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(description = "Order Model")
public class PlaceOrderDTO {
    @ApiModelProperty("Course ID List to Purchase, Can Be Single Course")
    @NotNull(message = "You Haven't Selected Courses Yet")
    @Size(min = 1, message = "You Haven't Selected Courses Yet")
    @Size(max = 10, message = "Maximum 10 Courses Can Be Selected at Once")
    private List<Long> courseIds;

    @ApiModelProperty("Coupon ID List Used for the Order, Can Be Empty")
    private List<Long> couponIds;

    @ApiModelProperty("Order id")
    @NotNull(message = "Order ID Cannot Be Empty")
    private Long orderId;
}