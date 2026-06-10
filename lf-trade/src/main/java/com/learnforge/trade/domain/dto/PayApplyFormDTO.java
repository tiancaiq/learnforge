package com.learnforge.trade.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Payment Application Info")
@Data
public class PayApplyFormDTO {
    @ApiModelProperty(value = "Order id",required = true)
    private Long orderId;
    @ApiModelProperty(value = "Payment Channel Code, wxPay, aliPay",required = true)
    private String payChannelCode;
}