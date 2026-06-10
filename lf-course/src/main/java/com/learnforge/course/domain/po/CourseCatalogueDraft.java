package com.learnforge.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
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
@TableName("course_catalogue_draft")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseCatalogueDraft implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Set Directory Basic Information
     *
     * @param cIndex Directory Sequence Number
     * @param name Directory Name
     * @param type Type
     * @param parentCatalogueId Parent Directory ID
     * @param courseId course id
     */
    public void setCataBaseInfo(Integer cIndex, String name, Integer type, Long parentCatalogueId, Long courseId){
        this.cIndex = cIndex;
        this.name = name;
        this.type = type;
        this.parentCatalogueId = parentCatalogueId;
        this.courseId = courseId;
    }

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
     * Directory Type 1: Chapter, 2: Section, 3: Test
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
     * Used for Chapter Sorting
     */
    private Integer cIndex;

    /**
     * In Seconds
     */
    private Integer mediaDuration;

    /**
     * Whether Can Be Modified, Directory Position Cannot Be Moved After Published
     */
    private Boolean canUpdate;
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
