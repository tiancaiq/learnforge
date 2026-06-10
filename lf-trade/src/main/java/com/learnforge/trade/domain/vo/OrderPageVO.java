package com.learnforge.trade.domain.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ApiModel(description = "Order pagination information")
public class OrderPageVO {
    @ApiModelProperty("Order id")
    private Long id;
    @ApiModelProperty("Order creation time")
    private LocalDateTime createTime;
    @ApiModelProperty("Actual paid amount")
    private Integer realAmount;
    @ApiModelProperty("Order detail amount")
    private Integer totalAmount;
    @ApiModelProperty("Order status 1: Pending payment, 2: Paid, 3: Closed, 4: Completed, 5: Registered")
    private Integer status;
    @ApiModelProperty("Order status description")
    private String statusDesc;
    @ApiModelProperty("Course details in order")
    private List<OrderDetailVO> details;
}
