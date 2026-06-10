package com.learnforge.learning.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Points Board VO")
public class PointsBoardVO {
    @ApiModelProperty("Rank")
    private Integer rank;
    @ApiModelProperty("Points")
    private Integer points;
    @ApiModelProperty("Board List")
    private List<PointsBoardItemVO> boardList;
}
