package com.learnforge.learning.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;

import com.learnforge.learning.domain.enums.LessonStatus;
import com.learnforge.learning.domain.enums.PlanStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>

 * </p>
 *

 * @since 2026-05-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("learning_lesson")
public class LearningLesson implements Serializable {

    private static final long serialVersionUID = 1L;

    /**

     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**

     */
    private Long userId;

    /**

     */
    private Long courseId;

    /**

     */
    private LessonStatus status;

    /**

     */
    private Integer weekFreq;

    /**

     */
    private PlanStatus planStatus;

    /**

     */
    private Integer learnedSections;

    /**

     */
    private Long latestSectionId;

    /**

     */
    private LocalDateTime latestLearnTime;

    /**

     */
    private LocalDateTime createTime;

    /**

     */
    private LocalDateTime expireTime;

    /**

     */
    private LocalDateTime updateTime;


}
