package com.learnforge.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.learnforge.learning.domain.enums.QuestionStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-05-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("interaction_question")
public class InteractionQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**

     */
    @TableField("title")
    private String title;

    /**

     */
    @TableField("description")
    private String description;

    /**

     */
    @TableField("course_id")
    private Long courseId;

    /**

     */
    @TableField("chapter_id")
    private Long chapterId;

    /**

     */
    @TableField("section_id")
    private Long sectionId;

    /**

     */
    @TableField("user_id")
    private Long userId;

    /**

     */
    @TableField("latest_answer_id")
    private Long latestAnswerId;

    /**

     */
    @TableField("answer_times")
    private Integer answerTimes;

    /**

     */
    @TableField("anonymity")
    private Boolean anonymity;

    /**

     */
    @TableField("hidden")
    private Boolean hidden;

    /**

     */
    @TableField("status")
    private QuestionStatus status;

    /**

     */
    @TableField("create_time")
    private LocalDateTime createTime;

    /**

     */
    @TableField("update_time")
    private LocalDateTime updateTime;


}
