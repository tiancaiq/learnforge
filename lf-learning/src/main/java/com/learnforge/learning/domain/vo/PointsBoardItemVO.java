package com.learnforge.learning.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Points Board Item VO")
public class PointsBoardItemVO {
    @ApiModelProperty("Points")
    private Integer points;
    @ApiModelProperty("Rank")
    private Integer rank;
    @ApiModelProperty("Name")
    private String name;
}
