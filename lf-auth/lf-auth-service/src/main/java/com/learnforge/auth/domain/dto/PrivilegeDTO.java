package com.learnforge.auth.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@ApiModel(description = "API privilege")
public class PrivilegeDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "Privilege id", example = "1")
    private Long id;
    @ApiModelProperty(value = "Privilege's menu id", example = "1")
    private Long menuId;
    @ApiModelProperty(value = "Privilege description", example = "Add employee")
    @NotNull(message = "Privilege description cannot be empty")
    private String intro;
    @ApiModelProperty(value = "API request method", example = "GET")
    @Pattern(regexp = "^GET|POST|PUT|DELETE$", message = "Request method must be uppercase")
    private String method;
    @ApiModelProperty(value = "API request path", example = "/account/staff")
    @NotNull(message = "uri cannot be empty")
    private String uri;
    @ApiModelProperty("Is internal privilege")
    private Boolean internal;
}
