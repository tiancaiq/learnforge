package com.learnforge.trade.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class PayChannelVO {

    @ApiModelProperty("Payment channel id")
    private Long id;
    @ApiModelProperty("Payment channel name")
    private String name;
    @ApiModelProperty("Payment channel code, unique identifier")
    private String channelCode;
    @ApiModelProperty("Channel priority, smaller number means higher priority")
    private Integer channelPriority;
    @ApiModelProperty("Channel icon")
    private String channelIcon;
}
