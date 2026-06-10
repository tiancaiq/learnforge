package com.learnforge.user.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "User information")
public class UserBasicVO {
    @ApiModelProperty(value = "User id", example = "1")
    private Long id;
    @ApiModelProperty(value = "User name/nickname", example = "Li Si")
    private String name;
    @ApiModelProperty(value = "User type, 1 - staff, 2 - regular student, 3 - teacher", example = "2")
    private Integer type;
    @ApiModelProperty(value = "Avatar", example = "default-user-icon.jpg")
    private String icon;
}
