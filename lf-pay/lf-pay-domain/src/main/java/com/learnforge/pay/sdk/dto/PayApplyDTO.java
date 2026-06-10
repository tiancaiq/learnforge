package com.learnforge.pay.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@ApiModel(description = "Payment application parameters")
public class PayApplyDTO {
    @ApiModelProperty("Business order number")
    @NotNull(message = "Business order id cannot be empty")
    private Long bizOrderNo;
    @ApiModelProperty("User id who placed the order")
    @NotNull(message = "Order user id cannot be empty")
    private Long bizUserId;
    @Min(value = 1, message = "Payment amount must be a positive number")
    @ApiModelProperty("Payment amount, in units of fen")
    private Integer amount;
    @NotNull(message = "Payment channel code cannot be empty")
    @ApiModelProperty("Payment channel code, for example: aliPay")
    private String payChannelCode;
    @ApiModelProperty("Payment method: 1-h5; 2-mini program; 3-public account; 4-scan code")
    @NotNull(message = "Payment method cannot be empty")
    private Integer payType;
    @ApiModelProperty("Product information in the order")
    @NotNull(message = "Product information in the order cannot be empty")
    private String orderInfo;
}
