package com.learnforge.learning.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Learning Plan DTO")
public class LearningPlanDTO {
    @NotNull
    @ApiModelProperty("Course ID")
    @Min(1)
    private Long courseId;
    @NotNull
    @Range(min = 1, max = 50)
    @ApiModelProperty("Freq")
    private Integer freq;
}
