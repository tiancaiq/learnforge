package com.learnforge.message.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * User notification record
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-20
 */
@Data
@ApiModel(description = "User private message form entity")
public class UserInboxFormDTO {

    @ApiModelProperty("Target user id")
    private Long userId;

    @ApiModelProperty("Private message content")
    private String content;
}
