package com.learnforge.course.domain.dto;

import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.common.validate.Checker;
import com.learnforge.common.validate.annotations.EnumValid;
import com.learnforge.course.constants.SubjectConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * Question Save Model
 * @author wusongsong
 * @since 2022/7/11 21:10
 * @version 1.0.0
 **/
@ApiModel("Question Save Model")
@Data
public class SubjectSaveDTO implements Checker {
    @ApiModelProperty("Question ID, Empty for New, Non-Empty for Update")
    private Long id;
    @ApiModelProperty("Name")
    @NotNull(message = "Question Cannot Be Empty, Please Set Question")
    @Size(max = 200, min = 5, message = "Question Length Should Be 5-200")
    private String name;
    @ApiModelProperty("Belongs to Question Category")
    @NotNull(message = "Question Category Cannot Be Empty, Please Set Question Category")
    private List<List<Long>> cates;
    @ApiModelProperty("Question Type")
    @NotNull(message = "Question Type Cannot Be Empty, Please Set Question Type")
    @EnumValid(enumeration = {1,2,3,4,5}, message = "Question type must be Single Choice, Multiple Choice, Indefinite Choice, True/False, or Subjective")
    private Integer subjectType;
    @ApiModelProperty("Question Difficulty")
    @NotNull(message = "Difficulty Cannot Be Empty")
    @EnumValid(enumeration = {1,2,3},message = "Question Difficulty Only Allows Easy, Medium, Hard")
    private Integer difficulty;
    @ApiModelProperty("Score")
    private Integer score;

    @ApiModelProperty("Course ID")
    private List<Long> courseIds;

    @ApiModelProperty("Options, Maximum 10")
    private List<String> options;

    @ApiModelProperty("Answer, True/False Question, First Element of Array is 1 Represents Correct, Others Represent Incorrect")
    @NotNull(message = "Question Answer Cannot Be Empty")
    private List<Integer> answers;
    @ApiModelProperty("Analysis")
    private String analysis;

    @Override
    public void check() {
        // Choice-based questions: Single Choice, Multiple Choice, or Indefinite Choice
        if(subjectType == SubjectConstants.Type.SIGNLE_CHOICE.getType() ||
                subjectType == SubjectConstants.Type.MUtiple_CHOICE.getType() ||
                subjectType == SubjectConstants.Type.NON_DIRECTIONAL_CHOICE.getType()){
            Integer answerOptionMax = answers.stream().max(Integer::compare).get();
            //Options Minimum 1, Maximum 10
            if(CollUtils.isEmpty(options) || options.size() > 10){
                throw new BizIllegalException("Minimum 1 Option, Maximum 10 Options");
            }
            //Multiple Choice Answer Cannot Exceed Number of Options
            if(answerOptionMax > options.size()){
                throw new BizIllegalException("Correct Answer Not Found in Options");
            }
            if(StringUtils.isNotEmpty(analysis)
                    && (StringUtils.length(analysis) < 5
                    || StringUtils.length(analysis) > 300)) {
                throw new BadRequestException("Answer Explanation Length Should Be 5-300");
            }
        }

    }
}
