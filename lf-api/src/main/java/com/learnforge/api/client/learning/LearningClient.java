package com.learnforge.api.client.learning;

import com.learnforge.api.client.learning.fallback.LearningClientFallback;
import com.learnforge.api.dto.leanring.LearningLessonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "learning-service", fallbackFactory = LearningClientFallback.class)
public interface LearningClient {

    /**
     * Statistics on number of learners for a course
     * @param courseId course id
     * @return number of learners
     */
    @GetMapping("/lessons/{courseId}/count")
    Integer countLearningLessonByCourse(@PathVariable("courseId") Long courseId);

    /**
     * Validate whether current user can learn the current course
     * @param courseId course id
     * @return lessonId, returns lessonId if enrolled, otherwise returns empty
     */
    @GetMapping("/lessons/{courseId}/valid")
    Long isLessonValid(@PathVariable("courseId") Long courseId);

    /**
     * Query learning progress for a specified course by current user
     * @param courseId course id
     * @return schedule information, learning records, and progress information
     */
    @GetMapping("/learning-records/course/{courseId}")
    LearningLessonDTO queryLearningRecordByCourse(@PathVariable("courseId") Long courseId);

}
