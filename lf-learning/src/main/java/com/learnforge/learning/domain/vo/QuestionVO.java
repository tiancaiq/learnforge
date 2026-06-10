package com.learnforge.learning.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel(description = "Question VO")
public class QuestionVO {
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
    @ApiModelProperty("Anonymity")
    private Boolean anonymity;
    @ApiModelProperty("User ID")
    private Long userId;
    @ApiModelProperty("User Name")
    private String userName;
    @ApiModelProperty("User Icon")
    private String userIcon;
    @ApiModelProperty("Latest Reply Content")
    private String latestReplyContent;
    @ApiModelProperty("Latest Reply User")
    private String latestReplyUser;
}