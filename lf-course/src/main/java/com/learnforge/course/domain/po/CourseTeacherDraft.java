package com.learnforge.course.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * CourseTeacherRelationDraft
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_teacher_draft")
public class CourseTeacherDraft implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * CourseTeacherRelationId
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * Course ID
     */
    private Long courseId;

    /**
     * Teacher ID
     */
    private Long teacherId;

    /**
     * Whether to Display on User End
     */
    private Integer isShow;

    /**
     * Sequence number
     */
    private Integer cIndex;

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

    /**
     * Logical Deletion
     */
    private Integer deleted;


}
