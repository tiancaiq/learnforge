package com.learnforge.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Course Content, Mainly Some Large Text
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_content_draft")
public class CourseContentDraft implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Course Content ID
     */
    private Long id;

    /**
     * Course Description
     */
    private String courseIntroduce;

    /**
     * Target Audience
     */
    private String usePeople;

    /**
     * Course Details
     */
    private String courseDetail;

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
//    private Integer deleted;


}
