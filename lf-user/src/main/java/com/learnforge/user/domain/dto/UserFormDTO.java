package com.learnforge.user.domain.dto;

import com.learnforge.api.dto.user.UserDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "User information modification form with password")
public class UserFormDTO extends UserDTO {
    @ApiModelProperty(value = "Original password", example = "123321")
    @NotNull
    private String oldPassword;
    @ApiModelProperty(value = "New password", example = "123321")
    @NotNull
    private String password;
}
