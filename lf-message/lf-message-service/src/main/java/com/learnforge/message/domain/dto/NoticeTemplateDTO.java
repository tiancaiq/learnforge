package com.learnforge.message.domain.dto;

import com.learnforge.common.domain.dto.BaseDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Notification template entity")
public class NoticeTemplateDTO extends BaseDTO {
    @ApiModelProperty("Primary key id, not required when adding")
    private Long id;
    @ApiModelProperty("Template name")
    private String name;
    @ApiModelProperty("Template code, for example: VERIFY_CODE")
    private String code;
    @ApiModelProperty("Notification type: 0-System notification, 1-Note notification, 2-Question and answer notification, 3-Other notification")
    private Integer type;
    @ApiModelProperty("Template status: 0-draft, 1-in use, 2-disabled")
    private Integer status;
    @ApiModelProperty("Notification title")
    private String title;
    @ApiModelProperty("Notification content")
    private String content;
    @ApiModelProperty("Whether it is a SMS template, default false")
    private Boolean isSmsTemplate;
}
