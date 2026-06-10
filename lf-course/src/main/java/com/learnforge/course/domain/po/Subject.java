package com.learnforge.course.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.StringUtils;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * Question
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("subject")
public class Subject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Question ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Question Stem
     */
    private String name;

    /**
     * Question type: 1-Single Choice, 2-Multiple Choice, 3-Indefinite Choice, 4-True/False, 5-Subjective
     */
    private Integer subjectType;

    /**
     * Difficulty, 1: Easy, 2: Medium, 3: Hard
     */
    private Integer difficulty;

    /**
     * MultipleChoiceAnswer1
     */
    private String option1;

    /**
     * MultipleChoiceAnswer2
     */
    private String option2;

    /**
     * MultipleChoiceAnswer3
     */
    private String option3;

    /**
     * MultipleChoiceAnswer4
     */
    private String option4;

    /**
     * MultipleChoiceAnswer5
     */
    private String option5;

    /**
     * MultipleChoiceAnswer6
     */
    private String option6;

    /**
     * MultipleChoiceAnswer7
     */
    private String option7;

    /**
     * MultipleChoiceAnswer8
     */
    private String option8;

    /**
     * MultipleChoiceAnswer9
     */
    private String option9;

    /**
     * MultipleChoiceAnswer10
     */
    private String option10;

    /**
     * Multiple Choice Correct Answers 1 to 10, If Multiple Answers, Use Commas to Separate, If True/False Question, 1: Correct, Others: Incorrect
     */
    private String answer;

    /**
     * Answer Analysis
     */
    private String analysis;

    /**
     * CorrectAnswerCount
     */
    private Integer correctTimes;

    /**
     * Score
     */
    private Integer score;

    /**
     * Department id
     */
    private Long depId;

    /**
     * Creation Time
     */
    private LocalDateTime createTime;

    /**
     * Update Time
     */
    private LocalDateTime updateTime;

    /**
     * ReferenceCount
     */
    private Integer useTimes;
    /**
     * AnswerCount
     */
    private Integer answerTimes;
    /**
     * Creator
     */
    private Long creater;

    /**
     * Updater
     */
    private Long updater;

    /**
     * Logical Deletion
     */
    @TableLogic
    private Integer deleted;

    public List<String> getOptions() {
        return Stream.of(option1, option2, option3, option4, option5, option6, option7, option8, option9, option10)
                .filter(StringUtils::isNotBlank).collect(Collectors.toList());
    }

    public List<Integer> getAnswers() {
        return CollUtils.convertToInteger(StringUtils.split(answer, ","));
    }
}
