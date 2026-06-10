package com.learnforge.pay.sdk.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * <p>
 * Payment channel vo object
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-26
 */
@Data
@ApiModel(description = "Payment channel information")
public class PayChannelDTO {

    @ApiModelProperty("Payment channel id")
    private Long id;
    @NotNull(message = "Channel name cannot be empty")
    @Length(min = 1, max = 50, message = "Channel name is too long")
    @ApiModelProperty("Payment channel name")
    private String name;
    @NotNull(message = "Channel code cannot be empty")
    @Pattern(regexp = "\\w{1,30}", message = "Channel code format error")
    @ApiModelProperty("Payment channel code, unique identifier")
    private String channelCode;
    @NotNull(message = "Channel priority cannot be empty")
    @ApiModelProperty("Channel priority, smaller number means higher priority")
    private Integer channelPriority;
    @NotNull(message = "Channel icon cannot be empty")
    @Length(min = 1, max = 255, message = "Channel icon address is too long")
    @ApiModelProperty("Channel icon")
    private String channelIcon;
    @ApiModelProperty("Payment channel status, 1: in use, 2: disabled. Default is 1 when added")
    private Integer status;
}
