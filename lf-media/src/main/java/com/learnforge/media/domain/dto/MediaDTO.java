package com.learnforge.media.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Video file information")
public class MediaDTO {
    @ApiModelProperty(value = "Database mediaId", example = "1")
    private Long id;

    @ApiModelProperty(value = "File name", example = "Practical-Redis-Course.mp4")
    private String filename;

    @ApiModelProperty(value = "Video duration, unit seconds", example = "57.23")
    private Float duration;

    @ApiModelProperty(value = "Video size, unit bytes", example = "1024")
    private Long size;
}
