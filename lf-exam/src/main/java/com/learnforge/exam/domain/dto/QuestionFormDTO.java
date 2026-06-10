package com.learnforge.exam.domain.dto;

import com.learnforge.common.validate.annotations.EnumValid;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * Question form entity
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(description = "Exam question form entity")
public class QuestionFormDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("Question id, not required for add")
    private Long id;

    @ApiModelProperty("Question Name, Question Stem")
    private String name;

    @ApiModelProperty("Question type: 1-Single Choice, 2-Multiple Choice, 3-Indefinite Choice, 4-True/False, 5-Subjective")
    @NotNull(message = "Question Type Cannot Be Empty, Please Set Question Type")
    @EnumValid(enumeration = {1,2,3,4,5}, message = "Question type error")
    private Integer type;

    @ApiModelProperty("Collection of third-level course category ids")
    private List<Long> cateIds;

    @ApiModelProperty("Difficulty, 1: Easy, 2: Medium, 3: Hard")
    @NotNull(message = "Difficulty Cannot Be Empty")
    @EnumValid(enumeration = {1,2,3},message = "Question difficulty error")
    private Integer difficulty;

    @ApiModelProperty("Score")
    private Integer score;

    @ApiModelProperty("Multiple Choice Options, JSON Array Format")
    private List<String> options;

    @ApiModelProperty("Multiple Choice Correct Answers 1 to 10, If Multiple Answers, Use Commas to Separate, If True/False Question, 1: Correct, Others: Incorrect")
    @NotNull(message = "Question Answer Cannot Be Empty")
    private String answer;

    @ApiModelProperty("Answer Analysis")
    @Size(max = 300, min = 5, message = "Answer Explanation Length Should Be 5-300")
    private String analysis;
}
