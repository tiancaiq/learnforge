package com.learnforge.pay.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@ApiModel(description = "Payment result")
@AllArgsConstructor
@Builder
public class RefundResultDTO {

    public static final int RUNNING = 1;
    public static final int FAILED = 2;
    public static final int SUCCESS = 3;
    public static final String OK = "ok";

    @ApiModelProperty("Refund status, 1: Refunding, 2: Refund failed, 3: Refund succeeded")
    private int status;
    @ApiModelProperty("Payment failure reason")
    private String msg;
    @ApiModelProperty("Business-side payment order number")
    private Long bizPayOrderId;
    @ApiModelProperty("Business-side refund order number")
    private Long bizRefundOrderId;
    @ApiModelProperty("Payment transaction serial number")
    private Long payOrderNo;
    @ApiModelProperty("Refund transaction serial number")
    private Long refundOrderNo;
    @ApiModelProperty("Payment channel")
    private String payChannel;
    @ApiModelProperty("Refund channel")
    private String refundChannel;

    public static RefundResultDTOBuilder success() {
        return builder().status(SUCCESS).msg(OK);
    }

    public static RefundResultDTOBuilder running() {
        return builder().status(RUNNING);
    }

    public static RefundResultDTOBuilder failed() {
        return builder().status(FAILED);
    }
}
