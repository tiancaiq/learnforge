package com.learnforge.user.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Student registration and password change form entity")
public class StudentFormDTO {

    @ApiModelProperty(value = "Phone number", example = "13800010004")
    private String cellPhone;

    @ApiModelProperty(value = "Password", example = "123456")
    private String password;

    @ApiModelProperty(value = "Verification code", example = "645632")
    private String code;
}
