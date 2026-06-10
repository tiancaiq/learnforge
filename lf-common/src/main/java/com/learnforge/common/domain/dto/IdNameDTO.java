package com.learnforge.common.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel(description = "Key-value pairs of id and name")
@NoArgsConstructor
@AllArgsConstructor
public class IdNameDTO {
    @ApiModelProperty("id")
    private Long id;
    @ApiModelProperty("name")
    private String name;
}
