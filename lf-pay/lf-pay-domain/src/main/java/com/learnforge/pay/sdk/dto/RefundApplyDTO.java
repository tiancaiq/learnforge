package com.learnforge.pay.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "Refund request parameters")
public class RefundApplyDTO {

    @ApiModelProperty("Business order id passed during payment")
    private Long bizOrderNo;
    @ApiModelProperty("The business order id to be refunded this time, since there may be split orders, this is the sub-order id")
    private Long bizRefundOrderNo;
    @ApiModelProperty("Refund amount of the sub-order, unit is cents")
    private Integer refundAmount;
}