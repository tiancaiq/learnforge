package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Course information in order item")
public class OrderDetailVO {
    @ApiModelProperty("Order item id")
    private Long id;
    @ApiModelProperty("Total order id")
    private Long orderId;
    @ApiModelProperty("Course ID")
    private Long courseId;
    @ApiModelProperty("Course Name")
    private String name;
    @ApiModelProperty("Cover")
    private String coverUrl;
    @ApiModelProperty("Course Price")
    private Integer price;
    @ApiModelProperty("Actual paid amount")
    private Integer realPayAmount;
    @ApiModelProperty("Refund Status")
    private Integer refundStatus;
    @ApiModelProperty("Coupon rule")
    private String couponDesc;
    @ApiModelProperty("Whether refund is allowed")
    private Boolean canRefund;
}