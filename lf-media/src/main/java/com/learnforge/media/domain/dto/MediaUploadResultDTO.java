package com.learnforge.media.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Media upload result")
public class MediaUploadResultDTO {

    @ApiModelProperty(value = "File unique identifier in cloud", example = "387702302659783576")
    private String fileId;
/*
    @ApiModelProperty(value = "Media playback address", example = "http://xxx.mp4")
    private String mediaUrl;

    @ApiModelProperty(value = "Media cover address", example = "http://xxx.jpg")
    private String coverUrl;

    @ApiModelProperty(value = "File name", example = "Redis-Best-Practices.mp4")
    // TODO Limit file name length
    private String filename;*/
}
