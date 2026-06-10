package com.learnforge.course.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * DirectoryAndExerciseModel
 * @author wusongsong
 * @since 2022/7/11 17:45
 * @version 1.0.0
 **/
@Data
@ApiModel(description = "CourseQuestionStatistics")
public class CataSubjectVO {
    @ApiModelProperty("SectionOrTestId")
    private Long cataId;
    @ApiModelProperty("SectionOrTestName")
    private String cataName;
    @ApiModelProperty("Type, 2: Section, 3: Test")
    private Integer type;
    @ApiModelProperty("Number of questions")
    private Integer subjectNum;
    @ApiModelProperty("Total score of questions")
    private Integer subjectScore;
}
