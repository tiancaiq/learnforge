package com.learnforge.exam.domain.po;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Question
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("question")
public class Question implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Question ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * Question Stem
     */
    private String name;

    /**
     * Question type: 1-Single Choice, 2-Multiple Choice, 3-Indefinite Choice, 4-True/False, 5-Subjective
     */
    private Integer type;

    /**
     * First-level course category id
     */
    private Long cateId1;

    /**
     * Second-level course category id
     */
    private Long cateId2;

    /**
     * Third-level course category id
     */
    private Long cateId3;

    /**
     * Difficulty, 1: Easy, 2: Medium, 3: Hard
     */
    private Integer difficulty;

    /**
     * CorrectAnswerCount
     */
    private Integer correctTimes;

    /**
     * Number of answers
     */
    private Integer answerTimes;

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
     * Creator
     */

    private Long creater;

    /**
     * Updater
     */

    private Long updater;


}
