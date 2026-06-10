package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@ApiModel(description = "Order placement response information")
public class PlaceOrderResultVO {
    @ApiModelProperty("Order number")
    private Long orderId;
    @ApiModelProperty("Payment amount")
    private Integer payAmount;
    @ApiModelProperty("Pending payment order, timeout time")
    private LocalDateTime payOutTime;
    @ApiModelProperty("Order status, 1: Pending payment, 2: Paid, 3: Closed, 4: Completed, 5: Registered, 6: Refund applied")
    private Integer status;
}

