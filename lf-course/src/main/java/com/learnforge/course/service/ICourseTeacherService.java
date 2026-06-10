package com.learnforge.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.course.domain.po.CourseTeacher;
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
public interface ICourseTeacherService extends IService<CourseTeacher> {

    /**
     * Query teacher course information
     * @param couserId Course id
     * @return Teacher information
     */
    List<CourseTeacherVO> queryTeachers(Long couserId);

    void deleteByCourseId(Long courserId);

    /**
     * Get teacher id list by course id and sort
     * @param courseId course id
     * @return Teacher information
     */
    List<Long> getTeacherIdOfCourse(Long courseId);


}
