package com.learnforge.promotion.domain.dto;

import com.learnforge.common.validate.annotations.EnumValid;
import com.learnforge.promotion.enums.DiscountType;
import com.learnforge.promotion.enums.ObtainType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@ApiModel(description = "Coupon Form DTO")
public class CouponFormDTO {

    @ApiModelProperty("Id")
    private Long id;

    @ApiModelProperty("Name")
    @NotNull(message = "Name is required")
    @Size(max = 20, min = 4, message = "Name has an invalid length")
    private String name;

    @ApiModelProperty("Specific")
    private Boolean specific;

    @ApiModelProperty("Scopes")
    private List<Long> scopes;

    @ApiModelProperty("Discount Type")
    @NotNull(message = "Discount Type is required")
    @EnumValid(enumeration = {1,2,3,4})
    private DiscountType discountType;

    @ApiModelProperty("Threshold Amount")
    private Integer thresholdAmount;
    @ApiModelProperty("Discount Value")
    private Integer discountValue;
    @ApiModelProperty("Max Discount Amount")
    private Integer maxDiscountAmount;

    @ApiModelProperty("Total Num")
    @Range(max = 5000, min = 1, message = "Total Num is outside the allowed range")
    private Integer totalNum;
    @ApiModelProperty("User Limit")
    @Range(max = 10, min = 1, message = "User Limit is outside the allowed range")
    private Integer userLimit;
    @ApiModelProperty("Obtain Way")
    @NotNull(message = "Obtain Way is required")
    @EnumValid(enumeration = {1, 2}, message = "Obtain Way is invalid")
    private ObtainType obtainWay;
}