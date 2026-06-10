package com.learnforge.trade.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Refund Application Data")
public class RefundFormDTO {
    @ApiModelProperty("Order Detail ID")
    @NotNull(message = "Please Select Refund Order")
    private Long orderDetailId;
    @ApiModelProperty("Refund Reason")
    @NotNull(message = "Please select the reason for refund")
    private String refundReason;
    @ApiModelProperty("Problem description")
    @NotNull(message = "Problem description cannot be empty")
    private String questionDesc;
}
