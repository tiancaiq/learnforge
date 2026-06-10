package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Classification id and name information")
public class CategoryBasicDTO {
    @ApiModelProperty(value = "Classification id", example = "1")
    private Long id;
    @ApiModelProperty(value = "Classification name", example = "Java")
    private String name;
    @ApiModelProperty(value = "Parent classification id", example = "0")
    private Long parentId;
}
