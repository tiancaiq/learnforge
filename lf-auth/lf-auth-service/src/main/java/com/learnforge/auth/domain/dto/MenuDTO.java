package com.learnforge.auth.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Menu form entity")
public class MenuDTO {
    @ApiModelProperty(value = "Menu id", example = "1")
    private Long id;

    @ApiModelProperty(value = "Parent menu id", example = "0")
    private Long parentId;

    @ApiModelProperty(value = "Menu text", example = "System management")
    private String label;

    @ApiModelProperty(value = "Menu path", example = "/sys/index")
    private String path;

    @ApiModelProperty(value = "Menu icon", example = "el-icon-sys")
    private String icon;

    @ApiModelProperty(value = "Menu order", example = "1")
    private Integer priority;
}
