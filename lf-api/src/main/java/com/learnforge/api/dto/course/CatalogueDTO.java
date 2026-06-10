package com.learnforge.api.dto.course;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wusongsong
 * @since 2022/7/11 16:42
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "Course directory")
public class CatalogueDTO {
    @ApiModelProperty("Chapter, section, exercise id")
    private Long id;
    @ApiModelProperty("Sequence number")
    private Integer index;
    @ApiModelProperty("Chapter and exercise name")
    private String name;
    @ApiModelProperty("Total course duration, unit in seconds")
    private Integer mediaDuration;
    @ApiModelProperty("Whether free preview is supported")
    private Boolean trailer;
    @ApiModelProperty("Media asset name")
    private String mediaName;
    @ApiModelProperty("Media asset id")
    private Long mediaId;
    @ApiModelProperty("Directory type 1: chapter, 2: section, 3: test")
    private Integer type;
    @ApiModelProperty("Number of questions")
    private Integer subjectNum;
    @ApiModelProperty("Total score of questions")
    private Integer totalScore;
    @ApiModelProperty("Whether can be modified, default is not modifiable")
    private Boolean canUpdate = false;
    @ApiModelProperty("All subsections and exercises in this chapter")
    private List<CatalogueDTO> sections;
}
