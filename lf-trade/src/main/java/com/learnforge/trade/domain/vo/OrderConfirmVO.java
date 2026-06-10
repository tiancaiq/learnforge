package com.learnforge.trade.domain.vo;

import com.learnforge.api.dto.promotion.CouponDiscountDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Order confirmation page information")
public class OrderConfirmVO {
    @ApiModelProperty("Order id")
    private Long orderId;
    @ApiModelProperty("Order total amount")
    private Integer totalAmount;
    @ApiModelProperty("Discount method")
    private List<CouponDiscountDTO> discounts;
    @ApiModelProperty("Courses included in the order")
    private List<OrderCourseVO> courses;
}
