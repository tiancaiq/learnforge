package com.learnforge.learning.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Sign Result VO")
public class SignResultVO {
    @ApiModelProperty("Sign Days")
    private Integer signDays;
    @ApiModelProperty("Sign Points")
    private Integer signPoints = 1;
    @ApiModelProperty("Reward Points")
    private Integer rewardPoints;

    @JsonIgnore
    public int totalPoints(){
        return signPoints + rewardPoints;
    }
}
