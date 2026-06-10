package com.learnforge.search.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Interested categories")
public class InterestCategoryVO {
    @ApiModelProperty(value = "Classification id", example = "1")
    private Long id;
    @ApiModelProperty(value = "Classification name", example = "Java")
    private String name;
}
