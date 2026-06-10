package com.learnforge.exam.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

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
@TableName(value = "question_detail", autoResultMap = true)
public class QuestionDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Question ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * Multiple Choice Options, JSON Array Format
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<String> options;

    /**
     * Multiple Choice Correct Answers 1 to 10, If Multiple Answers, Use Commas to Separate, If True/False Question, 1: Correct, Others: Incorrect
     */
    private String answer;

    /**
     * Answer Analysis
     */
    private String analysis;
}
