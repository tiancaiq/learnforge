package com.learnforge.learning.domain.dto;

import com.learnforge.common.validate.annotations.EnumValid;
import com.learnforge.learning.domain.enums.SectionType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@ApiModel(description = "Learning Record Form DTO")
public class LearningRecordFormDTO {

    @ApiModelProperty("Section Type")
    @NotNull(message = "Section Type is required")
    @EnumValid(enumeration = {1, 2}, message = "Section Type is invalid")
    private SectionType sectionType;

    @ApiModelProperty("Lesson ID")
    @NotNull(message = "Lesson ID is required")
    private Long lessonId;

    @ApiModelProperty("Section ID")
    @NotNull(message = "Section ID is required")
    private Long sectionId;

    @ApiModelProperty("Duration")
    private Integer duration;

    @ApiModelProperty("Moment")
    private Integer moment;

    @ApiModelProperty("Commit Time")
    private LocalDateTime commitTime;
}