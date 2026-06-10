package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Refund information")
public class RefundApplyPageVO {
    @ApiModelProperty("Refund ID")
    private Long id;
    @ApiModelProperty("Order Detail ID")
    private Long orderDetailId;
    @ApiModelProperty("Order id")
    private Long orderId;
    @ApiModelProperty("Refund Amount")
    private Integer refundAmount;
    @ApiModelProperty("Applicant")
    private String proposerName;
    @ApiModelProperty("Applicant phone number")
    private String proposerMobile;
    @ApiModelProperty("Refund application status")
    private Integer status;
    @ApiModelProperty("Refund application status description")
    private String refundStatusDesc;
    @ApiModelProperty("Refund application time")
    private LocalDateTime createTime;

    @ApiModelProperty("Approver")
    private String approverName;
    @ApiModelProperty("Approval time")
    private String approveTime;

    @ApiModelProperty("Refund success time")
    private LocalDateTime refundSuccessTime;
}