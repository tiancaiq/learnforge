package com.learnforge.api.dto.leanring;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Section information and learning progress")
public class LearningRecordDTO {
    @ApiModelProperty("Corresponding section id")
    private Long sectionId;
    @ApiModelProperty("Current playback duration of video, unit: seconds")
    private Integer moment;
    @ApiModelProperty("Whether learning is completed, default false")
    private Boolean finished;
}
