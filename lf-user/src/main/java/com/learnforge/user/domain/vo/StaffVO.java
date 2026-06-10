package com.learnforge.user.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Staff user")
public class StaffVO {
    @ApiModelProperty(value = "Primary key", example = "1")
    private Long id;
    @ApiModelProperty(value = "Avatar", example = "default-user-icon.jpg")
    private String icon;
    @ApiModelProperty(value = "Phone number", example = "13800010002")
    private String cellPhone;
    @ApiModelProperty(value = "Staff name", example = "user_138foo0002")
    private String name;
    @ApiModelProperty(value = "Role id", example = "5")
    private Long roleId;
    @ApiModelProperty(value = "Role name", example = "5")
    private String roleName;
    @ApiModelProperty(value = "Registration time", example = "2022-07-22")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "Account status", example = "0")
    private Integer status;
}
