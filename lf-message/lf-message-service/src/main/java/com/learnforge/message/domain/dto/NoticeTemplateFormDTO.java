package com.learnforge.message.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Notification template form entity")
public class NoticeTemplateFormDTO {
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
    @ApiModelProperty("Fill this when adding an SMS template, can include different SMS channels")
    private List<MessageTemplateFormDTO> messageTemplates;
    @ApiModelProperty("Fill this when deleting an added SMS template, only fill id")
    private List<Long> deleteMessageTemplates;
}
