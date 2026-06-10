package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wusongsong
 * @since 2022/8/18 11:40
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Question Brief Information")
public class SubjectSimpleVO {
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
}
