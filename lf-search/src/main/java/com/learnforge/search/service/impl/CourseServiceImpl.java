package com.learnforge.search.service.impl;

import com.learnforge.api.client.course.CourseClient;
import com.learnforge.api.dto.course.CourseSearchDTO;
import com.learnforge.common.utils.BeanUtils;
import com.learnforge.search.domain.po.Course;
import com.learnforge.search.repository.CourseRepository;
import com.learnforge.search.service.ICourseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CourseServiceImpl implements ICourseService {

    @Resource
    private CourseRepository courseRepository;
    @Resource
    private CourseClient courseClient;

    @Override
    public void handleCourseDelete(Long courseId) {
        // 1. Direct deletion
        courseRepository.deleteById(courseId);
    }

    @Override
    public void handleCourseUp(Long courseId) {
        // 1. Query course information by id
        CourseSearchDTO courseSearchDTO = courseClient.getSearchInfo(courseId);
        if (courseSearchDTO == null) {
            return;
        }
        // 2. Data conversion
        Course course = BeanUtils.toBean(courseSearchDTO, Course.class);
        course.setType(courseSearchDTO.getCourseType());
        // 3. Write to index library
        courseRepository.save(course);

    }

    @Override
    public void updateCourseSold(List<Long> courseIds, int amount) {
        courseRepository.incrementSold(courseIds, amount);
    }

    @Override
    public void handleCourseDeletes(List<Long> courseIds) {
        // 1. Direct deletion
        courseRepository.deleteByIds(courseIds);
    }
}
