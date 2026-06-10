package com.learnforge.api.dto.auth;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * Role table
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-06-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(description = "Role entity")
public class RoleDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @ApiModelProperty(value = "Primary key", example = "1")
    private Long id;

    /**
     * Role code, for example: admin
     */
    @ApiModelProperty(value = "Role code", example = "admin")
    private String code;

    /**
     * Role description
     */
    @ApiModelProperty(value = "Role name", example = "Teacher")
    private String name;
}
