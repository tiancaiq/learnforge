package com.learnforge.learning.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Points Board Query")
public class PointsBoardQuery extends PageQuery {
    @ApiModelProperty("Season")
    private Long season;
}
