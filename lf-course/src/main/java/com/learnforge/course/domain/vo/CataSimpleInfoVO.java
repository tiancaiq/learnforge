package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wusongsong
 * @since 2022/7/26 9:28
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "DirectorySimpleInfo")
@AllArgsConstructor
@NoArgsConstructor
public class CataSimpleInfoVO {
    @ApiModelProperty("Directory id")
    private Long id;
    @ApiModelProperty("Directory name")
    private String name;
    @ApiModelProperty("DirectorySequenceNumber1-1")
    private String index;
    @ApiModelProperty("Digital sequence number, does not include chapter sequence number")
    private Integer cIndex;
    @ApiModelProperty("NumericSequenceChapterNumber")
    private Integer chapterIndex;
}
