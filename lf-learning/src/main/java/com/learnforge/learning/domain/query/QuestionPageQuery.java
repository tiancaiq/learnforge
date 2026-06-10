package com.learnforge.learning.domain.query;

import com.learnforge.common.domain.query.PageQuery;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Question Page Query")
public class QuestionPageQuery extends PageQuery {

    @ApiModelProperty("Course ID")
    private Long courseId;
    @ApiModelProperty("Section ID")
    private Long sectionId;
    @ApiModelProperty("Only Mine")
    private Boolean onlyMine;
}
