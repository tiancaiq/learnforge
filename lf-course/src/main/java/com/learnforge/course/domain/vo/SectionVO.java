package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(description = "Section information and learning progress")
public class SectionVO {
    @ApiModelProperty("Corresponding Chapter ID")
    private Long id;
    @ApiModelProperty("Corresponding Chapter Name")
    private String name;
    @ApiModelProperty("Section Number")
    private Integer index;
    @ApiModelProperty("Corresponding Chapter Type, 2-Video (Section), 3-Exam")
    private Integer type;
    @ApiModelProperty("Total video duration, unit: seconds")
    private Integer mediaDuration;
    @ApiModelProperty("Media asset id")
    private Long mediaId;
    @ApiModelProperty("Whether free preview is supported")
    private Boolean trailer;
    @ApiModelProperty("Number of questions")
    private Integer subjectNum;
    @ApiModelProperty("Include Section Test")
    private Boolean hasTest;
    @ApiModelProperty("Current playback duration of video, unit: seconds")
    private Integer moment = 0;
    @ApiModelProperty("Whether learning is completed, default false")
    private Boolean finished;

    public Boolean getHasTest() {
        return subjectNum != null && subjectNum > 0;
    }
}