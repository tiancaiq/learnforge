package com.learnforge.message.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * Third-party SMS platform
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@Data
@ApiModel(description = "SMS template form entity")
public class MessageTemplateFormDTO {

    @ApiModelProperty("SMS sending template id, not required when adding")
    private Long id;

    @ApiModelProperty("Third-party SMS platform code")
    private String platformCode;

    @ApiModelProperty("Third-party platform SMS signature")
    private String signName;

    @ApiModelProperty("Third-party platform SMS template code")
    private String thirdTemplateCode;

    @ApiModelProperty("Template status: 0-draft, 1-in use, 2-disabled")
    private Integer status;

    @ApiModelProperty("SMS template name, not required if it is an SMS template under the notification template")
    private String name;

    @ApiModelProperty("SMS template preview content, not required if it is an SMS template under the notification template")
    private String content;
}
