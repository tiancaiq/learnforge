package com.learnforge.media.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Video file information")
public class MediaVO {
    @ApiModelProperty(value = "id", example = "1")
    private Long id;
    @ApiModelProperty(value = "File name", example = "File name.avi")
    private String filename;
    @ApiModelProperty(value = "Video cover", example = "default-cover-url.jpg")
    private String coverUrl;
    @ApiModelProperty(value = "Video duration, unit seconds", example = "57.23")
    private Float duration;
    @ApiModelProperty(value = "Video size, unit bytes", example = "1024")
    private Long size;
    @ApiModelProperty(value = "Number of Times Referenced", example = "10")
    private Integer useTimes;
    @ApiModelProperty(value = "Video status: 1-uploading, 2-uploaded, 3-processing", example = "2")
    private Integer status;
    @ApiModelProperty(value = "Creation Time", example = "2022-7-18 16:54:30")
    private LocalDateTime createTime;
    @ApiModelProperty(value = "Creator name", example = "Zhang San")
    private String creater;
}
