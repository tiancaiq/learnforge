package com.learnforge.course.service;

import com.learnforge.course.domain.dto.CourseTeacherSaveDTO;
import com.learnforge.course.domain.po.CourseTeacherDraft;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.course.domain.vo.CourseTeacherVO;

import java.util.List;

/**
 * <p>
 * Course-Teacher Relationship Draft Service Class
 * </p>
 *
 * @author wusongsong
 * @since 2022-07-20
 */
public interface ICourseTeacherDraftService extends IService<CourseTeacherDraft> {

    /**
     * Save specified teachers for a course
     * @param courseTeacherSaveDTO Teacher data
     */
    void save(CourseTeacherSaveDTO courseTeacherSaveDTO);

    /**
     * Query teachers for a specific course
     *
     * @param courseId course id
     * @param see Whether used for viewing
     * @return Teacher data
     */
    List<CourseTeacherVO> queryTeacherOfCourse(Long courseId,Boolean see);

    /**
     * Course Teacher Publish
     * @param courseId course id
     */
    void copyToShelf(Long courseId, Boolean isFirstShelf);

}
