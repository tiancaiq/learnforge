package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Question Data
 * @author wusongsong
 * @since 2022/7/11 20:24
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Question Pagination Data")
public class SubjectVO {
    @ApiModelProperty("Question ID")
    private Long id;
    @ApiModelProperty("Name")
    private String name;
    @ApiModelProperty("Category, Each Level Three Category Separated by /")
    private List<String> cates;
    @ApiModelProperty("Question Type")
    private Integer subjectType;
    @ApiModelProperty("Question Type Description")
    private String subjectTypeDesc;
    @ApiModelProperty("Question Difficulty Description")
    private String difficultDesc;
    @ApiModelProperty("Difficulty, 1: Easy, 2: Medium, 3: Hard")
    private String difficulty;
    @ApiModelProperty("Score")
    private Integer score;
    @ApiModelProperty("Usage Count")
    private Integer useTimes;
    @ApiModelProperty("AnswerCount")
    private Integer answerTimes;
    @ApiModelProperty("Updater")
    private String updaterName;
    @ApiModelProperty("Update Time")
    private LocalDateTime updateTime;
    @ApiModelProperty("Creation Time")
    private LocalDateTime createTime;
    @ApiModelProperty("Options")
    private List<String> options;
    @ApiModelProperty("Answer, True/False Question, First Element of Array is 1 Represents Correct, Others Represent Incorrect")
    private List<Integer> answers;
    @ApiModelProperty("Analysis")
    private String analysis;
    @ApiModelProperty("Accuracy Rate, Percentage Accurate to One Decimal Place")
    private String accuRate;
}
