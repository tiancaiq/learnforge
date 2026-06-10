package com.learnforge.exam.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * Question and business association table, for example, associate small section id with question id, one small section can have multiple questions
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-09-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("question_biz")
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class QuestionBiz implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Business ID, Need to Associate with a Business ID for the Question, for Example, Section ID
     */
    private Long bizId;

    /**
     * Question ID
     */
    private Long questionId;


}
