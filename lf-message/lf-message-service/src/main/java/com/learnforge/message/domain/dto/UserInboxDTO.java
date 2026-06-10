package com.learnforge.message.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * <p>
 * User notification record
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@ApiModel(description = "User inbox messages")
public class UserInboxDTO{

    @ApiModelProperty("Inbox message id")
    private Long id;

    @ApiModelProperty("Notification type: 0-System notification, 1-Note notification, 2-Question and answer notification, 3-Other notification, 4-Private message")
    private Integer type;

    @ApiModelProperty("Notification title")
    private String title;

    @ApiModelProperty("Notification or private message content")
    private String content;

    @ApiModelProperty("Whether it has been read")
    private Boolean isRead;

    @ApiModelProperty("Sender id of the message")
    private Long publisher;

    @ApiModelProperty("Inbox message id")
    private LocalDateTime pushTime;
}
