package com.learnforge.trade.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Order pagination query conditions")
public class OrderPageQuery extends PageQuery {
    @ApiModelProperty("Order status")
    private Integer status;
}
