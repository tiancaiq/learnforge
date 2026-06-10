package com.learnforge.promotion.domain.vo;

import com.learnforge.promotion.enums.DiscountType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Coupon VO")
public class CouponVO {
    @ApiModelProperty("Id")
    private Long id;
    @ApiModelProperty("Name")
    private String name;
    @ApiModelProperty("Specific")
    private Boolean specific;

    @ApiModelProperty("Discount Type")
    private DiscountType discountType;
    @ApiModelProperty("Threshold Amount")
    private Integer thresholdAmount;
    @ApiModelProperty("Discount Value")
    private Integer discountValue;
    @ApiModelProperty("Max Discount Amount")
    private Integer maxDiscountAmount;

    @ApiModelProperty("Term Days")
    private Integer termDays;
    @ApiModelProperty("Term End Time")
    private LocalDateTime termEndTime;

    @ApiModelProperty("Available")
    private Boolean available;

    @ApiModelProperty("Received")
    private Boolean received;
}
