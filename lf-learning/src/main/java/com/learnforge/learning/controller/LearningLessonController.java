package com.learnforge.learning.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.common.domain.query.PageQuery;
//import com.learnforge.learning.domain.dto.LearningPlanDTO;
import com.learnforge.learning.domain.dto.LearningPlanDTO;
import com.learnforge.learning.domain.vo.LearningLessonVO;
//import com.learnforge.learning.domain.vo.LearningPlanPageVO;
import com.learnforge.learning.domain.vo.LearningPlanPageVO;
import com.learnforge.learning.domain.vo.LearningPlanVO;
import com.learnforge.learning.service.ILearningLessonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>

 * </p>
 *

 * @since 2022-12-02
 */
@RestController
@RequestMapping("/lessons")
@Api(tags = "Learning Lesson Controller")
@RequiredArgsConstructor
public class LearningLessonController {

    private final ILearningLessonService lessonService;

    @GetMapping("/page")
    @ApiOperation("Query My Lessons")
    public PageDTO<LearningLessonVO> queryMyLessons(PageQuery query) {
        return lessonService.queryMyLessons(query);
    }

    @GetMapping("/now")
    @ApiOperation("Query My Current Lesson")
    public LearningLessonVO queryMyCurrentLesson() {
        return lessonService.queryMyCurrentLesson();
    }

    @GetMapping("/{courseId}")
    @ApiOperation("Query Lesson By Course ID")
    public LearningLessonVO queryLessonByCourseId(
            @ApiParam(value = "Field" ,example = "1") @PathVariable("courseId") Long courseId) {
        return lessonService.queryLessonByCourseId(courseId);
    }

    @DeleteMapping("/{courseId}")
    @ApiOperation("Delete Course From Lesson")
    public void deleteCourseFromLesson(
            @ApiParam(value = "Field" ,example = "1") @PathVariable("courseId") Long courseId) {
        lessonService.deleteCourseFromLesson(null, courseId);
    }

    @ApiOperation("Count Learning Lesson By Course")
    @GetMapping("/{courseId}/count")
    public Integer countLearningLessonByCourse(
            @ApiParam(value = "Field" ,example = "1") @PathVariable("courseId") Long courseId){
        return lessonService.countLearningLessonByCourse(courseId);
    }

    @ApiOperation("Is Lesson Valid")
    @GetMapping("/{courseId}/valid")
    public Long isLessonValid(
            @ApiParam(value = "Field" ,example = "1") @PathVariable("courseId") Long courseId){
        return lessonService.isLessonValid(courseId);
    }

//    @PostMapping("/plans")
//    public void createLearningPlans(@Valid @RequestBody LearningPlanDTO planDTO){
//        lessonService.createLearningPlan(planDTO.getCourseId(), planDTO.getFreq());
//    }
//
//    @GetMapping("/plans")
//    public LearningPlanPageVO queryMyPlans(PageQuery query){
//        return lessonService.queryMyPlans(query);
//    }

    @ApiOperation("Create Learning Plans")
    @PostMapping("plans")
    public void createLearningPlans(@Valid @RequestBody LearningPlanDTO planDTO){
        lessonService.createLearningPlans(planDTO.getCourseId(), planDTO.getFreq());

    }
    @ApiOperation("Query My Plans")
    @GetMapping("/plans")
    public LearningPlanPageVO queryMyPlans(PageQuery query){
        return lessonService.queryMyPlans(query);
    }
}
