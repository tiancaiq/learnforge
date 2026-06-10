package com.learnforge.course.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * Course-Question Relationship Draft
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("course_cata_subject")
public class CourseCataSubject implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Section Question Relationship ID
     */
    private Long id;

    /**
     * Course ID
     */
    private Long courseId;

    /**
     * Section ID
     */
    private Long cataId;

    /**
     * Question ID
     */
    private Long subjectId;


}
