package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(description = "Management order item detailed information")
public class OrderDetailAdminVO {
    @ApiModelProperty("Order item id")
    private Long id;
    @ApiModelProperty("Order id")
    private Long orderId;
    @ApiModelProperty("Refund ID")
    private Long refundApplyId;

    @ApiModelProperty("Payment transaction serial number")
    private Long payOrderNo;
    @ApiModelProperty("Refund transaction serial number")
    private Long refundOrderNo;

    @ApiModelProperty("Student nickname")
    private String studentName;
    @ApiModelProperty("Phone number")
    private String mobile;
    @ApiModelProperty("Refund applicant, format: role-name")
    private String refundProposerName;

    @ApiModelProperty("Payment method")
    private String payChannel;
    @ApiModelProperty("Refund method")
    private String refundChannel;
    @ApiModelProperty("Reason for refund failure")
    private String failedReason;

    @ApiModelProperty("Reason for refund application")
    private String refundReason;
    @ApiModelProperty("Refund application description")
    private String refundMessage;
    @ApiModelProperty("Approval Comment")
    private String remark;

    @ApiModelProperty("Order status, 1: Pending payment, 2: Paid, 3: Closed, 4: Completed, 5: Registered")
    private Integer status;
    @ApiModelProperty("Refund status, 1: Pending approval, 2: Cancel refund, 3: Approved refund, 4: Refuse refund, 5: Refund successful, 6: Refund failed")
    private Integer refundStatus;
    @ApiModelProperty("Status Description")
    private String message;

    @ApiModelProperty("Order progress node list")
    private List<OrderProgressNodeVO> nodes;

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
    @ApiModelProperty("Learning validity period")
    private LocalDateTime studyValidTime;

    @ApiModelProperty("Whether refund is allowed")
    private Boolean canRefund;
}