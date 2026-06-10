package com.learnforge.trade.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import com.learnforge.common.utils.DateUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Order details query conditions")
public class OrderDetailPageQuery extends PageQuery {
    @ApiModelProperty("Order Detail ID")
    private Long id;
    @ApiModelProperty("Order status: 1: Pending payment, 2: Paid, 3: Closed, 4: Completed, 5: Registered")
    private Integer status;
    @ApiModelProperty("Refund status: 1: Pending approval, 2: Cancel refund, 3: Approved refund, 4: Refuse refund, 5: Refund successful, 6: Refund failed")
    private Integer refundStatus;
    @ApiModelProperty("Payment method: wxPay: WeChat, aliPay: Alipay")
    private String payChannel;
    @ApiModelProperty("Phone number")
    private String mobile;
    @ApiModelProperty("Order start time")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime orderStartTime;
    @ApiModelProperty("Order end time")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime orderEndTime;
}
