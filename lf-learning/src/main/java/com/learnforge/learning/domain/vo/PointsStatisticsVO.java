package com.learnforge.learning.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Points Statistics VO")
public class PointsStatisticsVO {
    @ApiModelProperty("Type")
    private String type;
    @ApiModelProperty("Points")
    private Integer points;
    @ApiModelProperty("Max Points")
    private Integer maxPoints;
}
