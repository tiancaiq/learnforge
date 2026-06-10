package com.learnforge.user.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Paginated teacher information")
public class TeacherPageVO {
    @ApiModelProperty(value = "Teacher ID, also user ID", example = "1")
    private Long id;
    @ApiModelProperty(value = "TeacherName", example = "Luo Teacher")
    private String name;
    @ApiModelProperty(value = "Avatar", example = "default-user-icon.jpg")
    private String icon;
    @ApiModelProperty(value = "Phone number", example = "13980019001")
    private String cellPhone;
    @ApiModelProperty(value = "Position", example = "Instructor")
    private String job;
    @ApiModelProperty(value = "Introduction", example = "Black Horse Senior Java Instructor")
    private String intro;
    @ApiModelProperty(value = "Number of courses responsible for", example = "10")
    private Integer courseAmount;
    @ApiModelProperty(value = "Number of questions created", example = "18")
    private Integer examQuestionAmount;
    @ApiModelProperty(value = "Registration time", example = "2022-07-12")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "Account status, 0 - disabled, 1 - normal", example = "1")
    private Integer status;
    @ApiModelProperty(value = "Image photo address", example = "default-user-icon.jpg")
    private String photo;
}
