package com.learnforge.api.dto.user;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Login form entity")
public class LoginFormDTO {
    @ApiModelProperty(value = "Login method: 1-password login; 2-verification code login", example = "1", required = true)
    @NotNull
    private Integer type;
    @ApiModelProperty(value = "Username", example = "jack")
    private String username;
    @ApiModelProperty(value = "Phone number", example = "13800010001")
    private String cellPhone;
    @ApiModelProperty(value = "Password", example = "123", required = true)
    @NotNull
    private String password;
    @ApiModelProperty(value = "7-day passwordless login", example = "true")
    private Boolean rememberMe = false;
}
