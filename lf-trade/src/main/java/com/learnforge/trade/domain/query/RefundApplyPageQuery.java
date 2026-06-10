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
@ApiModel(description = "Refund application pagination parameters")
public class RefundApplyPageQuery extends PageQuery {

    @ApiModelProperty("Refund ID")
    private Long id;
    @ApiModelProperty("Refund status, 1: Pending approval, 2: Cancel refund, 3: Approved refund, 4: Refuse refund, 5: Refund successful, 6: Refund failed")
    private Integer refundStatus;
    @ApiModelProperty("Order Detail ID")
    private Long orderDetailId;
    @ApiModelProperty("Order id")
    private Long orderId;
    @ApiModelProperty("Student phone number")
    private String mobile;
    @ApiModelProperty("Application start time")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime applyStartTime;
    @ApiModelProperty("Application end time")
    @DateTimeFormat(pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT)
    private LocalDateTime applyEndTime;
}
