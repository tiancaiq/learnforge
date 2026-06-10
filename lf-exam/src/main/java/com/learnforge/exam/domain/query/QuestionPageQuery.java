package com.learnforge.exam.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Question pagination query conditions")
public class QuestionPageQuery extends PageQuery {
    @ApiModelProperty("Collection of third-level category ids")
    private List<Long> cateIds;
    @ApiModelProperty("Question type: 1-Single Choice, 2-Multiple Choice, 3-Indefinite Choice, 4-True/False, 5-Subjective")
    private List<Integer> types;
    @ApiModelProperty("Difficulty, 1: Easy, 2: Medium, 3: Hard")
    private Integer difficulty;
    @ApiModelProperty("Question name keyword")
    private String keyword;
    @ApiModelProperty("Question entry id")
    private Long creater;
}
