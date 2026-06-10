package com.learnforge.media.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Video playback signature information")
public class VideoPlayVO {
    @ApiModelProperty(value = "Video unique identifier", example = "12412534535143242")
    private String fileId;
    @ApiModelProperty(value = "Video cover", example = "xxx.xxx.xxx")
    private String signature;
}
