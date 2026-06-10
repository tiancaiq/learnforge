package com.learnforge.trade.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Refund Cancellation")
@Data
public class RefundCancelDTO {
    @ApiModelProperty("Refund Application ID, Order Detail ID and Refund Application ID, Choose One")
    private Long id;
    @ApiModelProperty("Order Detail ID, Order Detail ID and Refund Application ID, Choose One")
    private Long orderDetailId;
}