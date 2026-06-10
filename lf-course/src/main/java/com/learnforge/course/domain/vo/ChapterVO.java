package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(description = "ChapterInfo")
public class ChapterVO {
    @ApiModelProperty("ChapterId")
    private Long id;
    @ApiModelProperty("ChapterIndex")
    private Integer index;
    @ApiModelProperty("ChapterName")
    private String name;
    @ApiModelProperty("TotalVideoDurationInThisChapter")
    private Integer mediaDuration;

    private List<SectionVO> sections;
}
