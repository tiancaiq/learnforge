package com.learnforge.message.domain.dto;

import com.learnforge.common.domain.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * Third-party SMS platform
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "SMS template")
public class MessageTemplateDTO extends BaseDTO {

    @ApiModelProperty("SMS sending template id, not required when adding")
    private Long id;

    @ApiModelProperty("Third-party SMS push channel id")
    private Long platformCode;

    @ApiModelProperty("Third-party SMS push channel name")
    private String platformName;

    @ApiModelProperty("SMS template name")
    private String name;

    @ApiModelProperty("SMS template preview content")
    private String content;

    @ApiModelProperty("Third-party platform SMS signature")
    private String signName;

    @ApiModelProperty("Third-party platform SMS template code")
    private String thirdTemplateCode;

    @ApiModelProperty("Template status: 0-draft, 1-in use, 2-disabled")
    private Integer status;
}
