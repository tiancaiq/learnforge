package com.learnforge.promotion.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Coupon Query")
@Accessors(chain = true)
public class CouponQuery extends PageQuery {

    @ApiModelProperty("Type")
    private Integer type;

    @ApiModelProperty("Status")
    private Integer status;

    @ApiModelProperty("Name")
    private String name;
}