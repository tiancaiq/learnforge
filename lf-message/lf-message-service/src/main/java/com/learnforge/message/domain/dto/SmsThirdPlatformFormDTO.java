package com.learnforge.message.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * Third-party cloud communication platform
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@ApiModel(description = "Form entity of SMS third-party platform information")
public class SmsThirdPlatformFormDTO {

    @ApiModelProperty("SMS platform id, not required when adding")
    private Long id;
    @ApiModelProperty("SMS platform name")
    private String name;
    @ApiModelProperty("SMS platform code, for example: ali")
    private String code;
    @ApiModelProperty("The smaller the number, the higher the priority, minimum is 0")
    private Integer priority;
    @ApiModelProperty("SMS platform status: 0-Disabled, 1-Enabled")
    private Integer status;
}
