package com.learnforge.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.time.LocalDateTime;

@ApiModel(description = "DTO base properties")
public class BaseDTO {
    @ApiModelProperty("Creator id")
    private Long creater;
    @ApiModelProperty("Updater id")
    private Long updater;
    @ApiModelProperty("Creation Time")
    private LocalDateTime createTime;
    @ApiModelProperty("Update Time")
    private LocalDateTime updateTime;
}
