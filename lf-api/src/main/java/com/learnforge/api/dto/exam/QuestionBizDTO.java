package com.learnforge.api.dto.exam;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@ApiModel(description = "Question and Business Association Info")
@Accessors(chain = true)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor
public class QuestionBizDTO{

    @ApiModelProperty("Business ID, Need to Associate with a Business ID for the Question, for Example, Section ID")
    private Long bizId;

    @ApiModelProperty("Question ID")
    private Long questionId;

}
