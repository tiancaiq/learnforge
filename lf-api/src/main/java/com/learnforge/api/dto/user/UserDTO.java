package com.learnforge.api.dto.user;

import com.learnforge.common.constants.RegexConstants;
import com.learnforge.common.validate.annotations.EnumValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@ApiModel(description = "User details")
public class UserDTO {
    @ApiModelProperty(value = "User id", example = "1")
    private Long id;
    @ApiModelProperty(value = "Phone", example = "13890011009")
    @Pattern(regexp = RegexConstants.PHONE_PATTERN, message = "Phone number format error")
    private String cellPhone;
    @ApiModelProperty(value = "User name/nickname", example = "Li Si")
    private String name;
    @ApiModelProperty(value = "User type, 1-other staff, 2-regular student, 3-teacher", example = "2")
    @EnumValid(enumeration = {1,2,3}, message = "User type error")
    @NotNull
    private Integer type;
    @ApiModelProperty(value = "Role id, teachers and students do not need to fill", example = "5")
    private Long roleId;
    @ApiModelProperty(value = "Avatar", example = "default-user-icon.jpg")
    private String icon;
    @ApiModelProperty(value = "Position", example = "Instructor")
    private String job;
    @ApiModelProperty(value = "Personal introduction", example = "Black Horse Senior Java Instructor")
    private String intro;
    @ApiModelProperty(value = "Image URL", example = "default-teacher-photo.jpg")
    private String photo;
    @ApiModelProperty(value = "Username", example = "13800010004")
    private String username;
    @ApiModelProperty(value = "Email")
    @Email
    private String email;
    @ApiModelProperty(value = "QQ number")
    private String qq;
    @ApiModelProperty(value = "Province")
    private String province;
    @ApiModelProperty(value = "City")
    private String city;
    @ApiModelProperty(value = "District")
    private String district;
    @ApiModelProperty(value = "Gender: 0-male, 1-female", example = "0")
    @EnumValid(enumeration = {0, 1}, message = "Gender format is incorrect")
    private Integer gender;
}
