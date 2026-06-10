package com.learnforge.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * Directory Draft
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_catalogue")
public class CourseCatalogue implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Course Directory ID
     */
    private Long id;

    /**
     * Directory name
     */
    private String name;

    /**
     * Support Trial Viewing
     */
    private Integer trailer;

    /**
     * Course ID
     */
    private Long courseId;

    /**
     * Directory type 1: chapter, 2: section, 3: test
     */
    private Integer type;

    /**
     * Chapter ID, Only Sections and Tests Have This Value, Chapters Do Not, Chapters Default to 0
     */
    private Long parentCatalogueId;

    /**
     * Media asset id
     */
    private Long mediaId;

    /**
     * Video ID
     */
    private Long videoId;

    /**
     * Video Name
     */
    private String videoName;

    /**
     * Live Start Time
     */
    private LocalDateTime livingStartTime;

    /**
     * Live End Time
     */
    private LocalDateTime livingEndTime;

    /**
     * Support Replay
     */
    private Integer playBack;

    /**
     * Video Duration, in Seconds
     */
    private Integer mediaDuration;

    /**
     * Used for Chapter Sorting
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
