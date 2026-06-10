package com.learnforge.learning.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Question Admin VO")
public class QuestionAdminVO {
    @ApiModelProperty("Id")
    private Long id;
    @ApiModelProperty("Title")
    private String title;
    @ApiModelProperty("Description")
    private String description;
    @ApiModelProperty("Answer Times")
    private Integer answerTimes;
    @ApiModelProperty("Create Time")
    private LocalDateTime createTime;
    @ApiModelProperty("Status")
    private Integer status;
    @ApiModelProperty("Hidden")
    private Boolean hidden;

    @ApiModelProperty("User Name")
    private String userName;
    @ApiModelProperty("User Icon")
    private String userIcon;
    @ApiModelProperty("Course Name")
    private String courseName;
    @ApiModelProperty("Chapter Name")
    private String chapterName;
    @ApiModelProperty("Section Name")
    private String sectionName;
    @ApiModelProperty("Category Name")
    private String categoryName;
}

