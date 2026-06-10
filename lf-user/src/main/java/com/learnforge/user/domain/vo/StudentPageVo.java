package com.learnforge.user.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Student information")
public class StudentPageVo {
    @ApiModelProperty(value = "Student ID, also user ID", example = "1")
    private Long id;
    @ApiModelProperty(value = "Student name", example = "Zhang San")
    private String name;
    @ApiModelProperty(value = "Avatar", example = "default-icon.jpg")
    private String icon;
    @ApiModelProperty(value = "Phone number", example = "13800010004")
    private String cellPhone;
    @ApiModelProperty(value = "Gender: 0-male, 1-female", example = "0")
    private Integer gender;
    @ApiModelProperty(value = "Number of purchased/registered courses", example = "12")
    private Integer courseAmount;
    @ApiModelProperty(value = "Registration time", example = "2022-07-12")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "Account status, 0 - disabled, 1 - normal", example = "1")
    private Integer status;
}