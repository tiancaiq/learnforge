package com.learnforge.learning.domain.vo;

import com.learnforge.common.domain.dto.PageDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@ApiModel(description = "Learning Plan Page VO")
public class LearningPlanPageVO extends PageDTO<LearningPlanVO> {
    @ApiModelProperty("Week Points")
    private Integer weekPoints;
    @ApiModelProperty("Week Finished")
    private Integer weekFinished;
    @ApiModelProperty("Week Total Plan")
    private Integer weekTotalPlan;

    public LearningPlanPageVO() {
    }

    public LearningPlanPageVO pageInfo(Long total, Long pages, List<LearningPlanVO> list) {
        this.total = total;
        this.pages = pages;
        this.list = list;
        return this;
    }

    public LearningPlanPageVO pageInfo(PageDTO<LearningPlanVO> pageDTO) {
        this.total = pageDTO.getTotal();
        this.pages = pageDTO.getPages();
        this.list = pageDTO.getList();
        return this;
    }

}