package com.learnforge.api.dto.leanring;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "Learning Schedule Progress Info")
public class LearningLessonDTO {
    @ApiModelProperty("Schedule ID")
    private Long id;
    @ApiModelProperty("Latest Learned Section ID")
    private Long latestSectionId;
    @ApiModelProperty("Record of learned sections")
    private List<LearningRecordDTO> records;
}
