package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@ApiModel(description = "Refund application detailed information")
public class RefundApplyVO {
    @ApiModelProperty("Refund ID")
    private Long id;
    @ApiModelProperty("Sub Order ID")
    private Long orderDetailId;
    @ApiModelProperty("Order id")
    private Long orderId;
    @ApiModelProperty("Payment transaction serial number")
    private Long payOrderNo;

    @ApiModelProperty("Payment method")
    private String payChannel;
    @ApiModelProperty("Refund method")
    private String refundChannel;
    @ApiModelProperty("Refund transaction serial number")
    private Long refundOrderNo;

    @ApiModelProperty("Reason for refund application")
    private String refundReason;
    @ApiModelProperty("Refund application explanation")
    private String questionDesc;

    @ApiModelProperty("Student nickname")
    private String studentName;
    @ApiModelProperty("Phone number")
    private String mobile;
    @ApiModelProperty("Refund applicant, format: role-name")
    private String refundProposerName;

    @ApiModelProperty("Order time")
    private LocalDateTime orderTime;
    @ApiModelProperty("Payment time")
    private LocalDateTime paySuccessTime;
    @ApiModelProperty("Refund application time")
    private LocalDateTime createTime;
    @ApiModelProperty("Refund approval time")
    private LocalDateTime approveTime;

    @ApiModelProperty("Status Description")
    private String message;
    @ApiModelProperty("Approval Comment")
    private String approveOpinion;
    @ApiModelProperty("Approval Comment")
    private String remark;

    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("Course Price")
    private Integer price;
    @ApiModelProperty("Actual paid amount")
    private Integer realPayAmount;
    @ApiModelProperty("Coupon rule")
    private String couponDesc;
    @ApiModelProperty("Total discount amount")
    private Integer discountAmount;


    @ApiModelProperty("Refund status: 1: Pending approval, 2: Cancel refund, 3: Approved refund, 4: Refuse refund, 5: Refund successful, 6: Refund failed")
    private Integer status;
    @ApiModelProperty("Reason for refund failure")
    private String failedReason;
}
