package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Order details pagination result")
public class OrderDetailPageVO {
    @ApiModelProperty("Order Detail ID")
    private Long id;
    @ApiModelProperty("Order id")
    private Long orderId;
    @ApiModelProperty("Student name")
    private String name;
    @ApiModelProperty("Phone number")
    private String mobile;
    @ApiModelProperty("Order amount, that is, the original course price")
    private Integer price;
    @ApiModelProperty("Actual paid amount")
    private Integer realPayAmount;
    @ApiModelProperty("Order status 1: Pending payment, 2: Paid, 3: Closed, 4: Completed, 5: Registered")
    private Integer status;
    @ApiModelProperty("Order status description")
    private String statusDesc;
    @ApiModelProperty("Refund status 1: Pending payment, 2: Paid, 3: Closed, 4: Completed, 5: Registered, 0: No refund status")
    private Integer refundStatus;
    @ApiModelProperty("Refund status description")
    private String refundStatusDesc;
    @ApiModelProperty("Order time")
    private LocalDateTime createTime;
    @ApiModelProperty("Payment method")
    private String payChannel;
}