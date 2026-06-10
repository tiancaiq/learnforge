package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Exam Record
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-07-18
 */
@Data
@ApiModel(description = "Exam Question Details")
public class SubjectDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("Question ID")
    private Long id;

    @ApiModelProperty("Question Stem")
    private String name;

    @ApiModelProperty("Multiple Choice Options")
    private List<String> options;

    @ApiModelProperty("Score")
    private Integer score;

    @ApiModelProperty("Question type: 1-Single Choice, 2-Multiple Choice, 3-Indefinite Choice, 4-True/False, 5-Subjective")
    private Integer subjectType;

    @ApiModelProperty("Difficulty, 1: Easy, 2: Medium, 3: Hard")
    private Integer difficulty;

    @ApiModelProperty("Analysis")
    private String analysis;

    @ApiModelProperty("Multiple Choice Answers, 0 Corresponds to A, 1 Corresponds to B, Can Fill Multiple")
    private List<Integer> answers;
}
