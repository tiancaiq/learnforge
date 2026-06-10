package com.learnforge.api.dto.exam;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(description = "Question Data")
public class QuestionDTO {

    @ApiModelProperty("Question ID")
    private Long id;

    @ApiModelProperty("Question Name, Question Stem")
    private String name;

    @ApiModelProperty("Question type: 1-Single Choice, 2-Multiple Choice, 3-Indefinite Choice, 4-True/False, 5-Subjective")
    private String type;

    @ApiModelProperty("Difficulty, 1: Easy, 2: Medium, 3: Hard")
    private Integer difficulty;

    @ApiModelProperty("Score")
    private Integer score;

    @ApiModelProperty("Multiple Choice Options, JSON Array Format")
    private List<String> options;

    @ApiModelProperty("Multiple Choice Correct Answers 1 to 10, If Multiple Answers, Use Commas to Separate, If True/False Question, 1: Correct, Others: Incorrect")
    private String answer;

    @ApiModelProperty("Answer Analysis")
    private String analysis;
}
