package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ApiModel("Section Info, Including Course ID and Media Asset ID")
@AllArgsConstructor
@NoArgsConstructor
public class SectionInfoDTO {
    @ApiModelProperty("Course ID")
    private Long courseId;
    @ApiModelProperty("Media asset id")
    private Long mediaId;
    @ApiModelProperty("Whether free preview is supported")
    private Boolean trailer;
    @ApiModelProperty("Free Duration, 0 for Non-Free, Unit: Minutes")
    private Integer freeDuration;
}
