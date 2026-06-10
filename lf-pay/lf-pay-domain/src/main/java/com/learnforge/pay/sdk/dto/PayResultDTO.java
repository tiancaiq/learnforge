package com.learnforge.pay.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@ApiModel(description = "Payment result")
@AllArgsConstructor
public class PayResultDTO {

    public static final int PAYING = 1;
    public static final int FAILED = 2;
    public static final int SUCCESS = 3;
    public static final String OK = "ok";

    @ApiModelProperty("Payment result, 1: payment in progress, 2: payment failed, 3: payment successful")
    private final int status;
    @ApiModelProperty("Payment failure reason")
    private final String msg;
    @ApiModelProperty("Business order number")
    private final Long bizOrderId;
    @ApiModelProperty("Business order number")
    private final Long payOrderNo;
    @ApiModelProperty("Payment channel")
    private final String payChannel;
    @ApiModelProperty("Payment successful time")
    private final LocalDateTime successTime;
}
