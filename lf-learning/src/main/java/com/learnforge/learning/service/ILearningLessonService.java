package com.learnforge.learning.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.domain.query.PageQuery;
import com.learnforge.learning.domain.po.LearningLesson;
import com.learnforge.learning.domain.vo.LearningLessonVO;
import com.learnforge.learning.domain.vo.LearningPlanPageVO;
import org.hibernate.validator.constraints.Range;
//import com.learnforge.learning.domain.vo.LearningPlanPageVO;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>

 * </p>
 *

 * @since 2022-12-02
 */
public interface ILearningLessonService extends IService<LearningLesson> {

    void addUserLessons(Long userId, List<Long> courseIds);

    PageDTO<LearningLessonVO> queryMyLessons(PageQuery query);

    LearningLessonVO queryMyCurrentLesson();

    LearningLessonVO queryLessonByCourseId(Long courseId);

    void deleteCourseFromLesson(Long userId, Long courseId);

    Integer countLearningLessonByCourse(Long courseId);

    Long isLessonValid(Long courseId);

    LearningLesson queryByUserAndCourseId(Long userId, Long courseId);
//
//    void createLearningPlan(Long courseId, Integer freq);

    LearningLesson queryByUserIdAndCourseId(Long userId, Long courseId);

    void createLearningPlans(@NotNull @Min(1) Long courseId, @NotNull @Range(min = 1, max = 50) Integer freq);

    LearningPlanPageVO queryMyPlans(PageQuery query);

//    LearningPlanPageVO queryMyPlans(PageQuery query);
}