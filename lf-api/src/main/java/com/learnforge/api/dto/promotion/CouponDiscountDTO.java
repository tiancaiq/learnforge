package com.learnforge.api.dto.promotion;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@ApiModel(description = "Coupon Discount DTO")
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CouponDiscountDTO {
    @ApiModelProperty("Ids")
    private List<Long> ids = new ArrayList<>();
    @ApiModelProperty("Rules")
    private List<String> rules = new ArrayList<>();
    @ApiModelProperty("Discount Amount")
    private Integer discountAmount = 0;
    @ApiModelProperty("Discount Detail")
    private Map<Long, Integer> discountDetail;
}
