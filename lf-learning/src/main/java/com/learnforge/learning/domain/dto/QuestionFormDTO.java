package com.learnforge.learning.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(description = "Question Form DTO")
public class QuestionFormDTO {
    @ApiModelProperty("Course ID")
    @NotNull(message = "Course ID is required")
    private Long courseId;
    @ApiModelProperty("Chapter ID")
    @NotNull(message = "Chapter ID is required")
    private Long chapterId;
    @ApiModelProperty("Section ID")
    @NotNull(message = "Section ID is required")
    private Long sectionId;
    @ApiModelProperty("Title")
    @NotNull(message = "Title is required")
    @Length(min = 1, max = 254, message = "Title is invalid")
    private String title;
    @ApiModelProperty("Description")
    @NotNull(message = "Description is required")
    private String description;
    @ApiModelProperty("Anonymity")
    private Boolean anonymity;
}
