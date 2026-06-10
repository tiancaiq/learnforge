package com.learnforge.exam.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(description = "Exam details data")
public class QuestionDetailVO {

    @ApiModelProperty("Question ID")
    private Long id;

    @ApiModelProperty("Question Name, Question Stem")
    private String name;

    @ApiModelProperty("Question type: 1-Single Choice, 2-Multiple Choice, 3-Indefinite Choice, 4-True/False, 5-Subjective")
    private Integer type;

    @ApiModelProperty("First-level course category id")
    private Long cateId1;

    @ApiModelProperty("Second-level course category id")
    private Long cateId2;

    @ApiModelProperty("Third-level course category id")
    private Long cateId3;
    @ApiModelProperty("Collection of third-level course category names")
    private List<String> categories;

    @ApiModelProperty("Difficulty, 1: Easy, 2: Medium, 3: Hard")
    private Integer difficulty;

    @ApiModelProperty("Score")
    private Integer score;

    @ApiModelProperty("ReferenceCount")
    private Integer useTimes;

    @ApiModelProperty("CorrectAnswerCount")
    private Integer correctTimes;

    @ApiModelProperty("Number of answers")
    private Integer answerTimes;

    @ApiModelProperty("Updater")
    private String updater;

    @ApiModelProperty("Update Time")
    private LocalDateTime updateTime;

    @ApiModelProperty("Multiple Choice Options, JSON Array Format")
    private List<String> options;

    @ApiModelProperty("Multiple Choice Correct Answers 1 to 10, If Multiple Answers, Use Commas to Separate, If True/False Question, 1: Correct, Others: Incorrect")
    private String answer;

    @ApiModelProperty("Answer Analysis")
    private String analysis;
}
