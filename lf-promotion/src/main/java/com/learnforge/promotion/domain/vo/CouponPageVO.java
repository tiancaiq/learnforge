package com.learnforge.promotion.domain.vo;

import com.learnforge.promotion.enums.CouponStatus;
import com.learnforge.promotion.enums.DiscountType;
import com.learnforge.promotion.enums.ObtainType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Coupon Page VO")
public class CouponPageVO {
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

    @ApiModelProperty("Obtain Way")
    private ObtainType obtainWay;
    @ApiModelProperty("Used")
    private Integer usedNum;
    @ApiModelProperty("Issue Num")
    private Integer issueNum;
    @ApiModelProperty("Total Num")
    private Integer totalNum;

    @ApiModelProperty("Create Time")
    private LocalDateTime createTime;
    @ApiModelProperty("Issue Begin Time")
    private LocalDateTime issueBeginTime;
    @ApiModelProperty("Issue End Time")
    private LocalDateTime issueEndTime;

    @ApiModelProperty("Term Days")
    private Integer termDays;
    @ApiModelProperty("Term Begin Time")
    private LocalDateTime termBeginTime;
    @ApiModelProperty("Term End Time")
    private LocalDateTime termEndTime;

    @ApiModelProperty("Status")
    private CouponStatus status;
}
