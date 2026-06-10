package com.learnforge.user.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "User details")
public class UserDetailVO {
    @ApiModelProperty(value = "User id", example = "1")
    private Long id;
    @ApiModelProperty(value = "Name", example = "Zhang San")
    private String name;
    @ApiModelProperty(value = "Avatar", example = "default-icon.jpg")
    private String icon;
    @ApiModelProperty(value = "Phone number", example = "13800010004")
    private String cellPhone;
    @ApiModelProperty(value = "Username", example = "13800010004")
    private String username;
    @ApiModelProperty(value = "Email")
    private String email;
    @ApiModelProperty(value = "QQ number")
    private String qq;
    @ApiModelProperty(value = "Personal introduction")
    private String intro;
    @ApiModelProperty(value = "Province")
    private String province;
    @ApiModelProperty(value = "City")
    private String city;
    @ApiModelProperty(value = "District")
    private String district;
    @ApiModelProperty(value = "Gender: 0-male, 1-female", example = "0")
    private Integer gender;
    @ApiModelProperty(value = "Registration time", example = "2022-07-12")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "Role name", example = "Teacher")
    private String roleName;
}
