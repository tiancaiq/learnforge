package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Question Details
 *
 * @author wusongsong
 * @since 2022/7/11 20:54
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Question Details")
public class SubjectInfoVO {
    @ApiModelProperty("Question ID")
    private Long id;
    @ApiModelProperty("Name")
    private String name;
    @ApiModelProperty("Belongs to Question Category")
    private List<CateSimpleInfoVO> cates;
    @ApiModelProperty("Question Type")
    private Integer subjectType;
    @ApiModelProperty("Question Difficulty")
    private Integer difficulty;
    @ApiModelProperty("Score")
    private Integer score;
    @ApiModelProperty("Update Time")
    private LocalDateTime updateTime;
    @ApiModelProperty("Updater")
    private String updaterName;
    @ApiModelProperty("Course Name Information")
    private List<CourseSimpleInfoVO> courses;

    @ApiModelProperty("Options")
    private List<String> options;
    @ApiModelProperty("Answer, True/False Question, First Element of Array is 1 Represents Correct, Others Represent Incorrect")
    private List<Integer> answers;
    @ApiModelProperty("Analysis")
    private String analysis;
    @ApiModelProperty("Course ID List")
    private List<Long> courseIds;
    @ApiModelProperty(value = "Number of Times Referenced", example = "10")
    private Integer useTimes;
    @ApiModelProperty("AnswerCount")
    private Integer answerTimes;
    @ApiModelProperty("Accuracy Rate")
    private Double correctRate;


}
