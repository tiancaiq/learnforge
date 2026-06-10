package com.learnforge.course.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName SubjectPageParamDTO
 * @Author wusongsong
 * @Date 2022/7/11 20:07
 * @Version
 **/
@Data
@ApiModel(description = "Question Pagination Parameters")
public class SubjectPageParamDTO {
    @ApiModelProperty("Primary Course Category")
    private Long firstCateId;
    @ApiModelProperty("Secondary Course Category")
    private Long secondCateId;
    @ApiModelProperty("List of Tertiary Course Category IDs")
    private List<Long> thirdCateIds;
    @ApiModelProperty("Difficulty, 1: Easy, 2: Medium, 3: Hard")
    private Integer difficulty;
    @ApiModelProperty("Name")
    private String name;
    @ApiModelProperty("Question type: 1-Single Choice, 2-Multiple Choice, 3-Indefinite Choice, 4-True/False; separate multiple values with commas")
    private String subjectTypes;
    @ApiModelProperty("Whether to Select All Current User, Default Search All")
    private Boolean own;
}
