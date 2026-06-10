package com.learnforge.api.client.trade;

import com.learnforge.api.client.trade.fallback.TradeClientFallback;
import com.learnforge.api.dto.course.CoursePurchaseInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@FeignClient(value = "trade-service", fallbackFactory = TradeClientFallback.class)
public interface TradeClient {
    /**
     * Statistics on number of enrollments for a specified course
     * @param courseIdList collection of course ids
     * @return statistical result
     */
    @GetMapping("/order-details/enrollNum")
    Map<Long, Integer> countEnrollNumOfCourse(@RequestParam("courseIdList") List<Long> courseIdList);

    /**
     * Statistics on number of enrolled courses for a specified student
     * @param studentIds collection of student ids
     * @return statistical result
     */
    @GetMapping("/order-details/enrollCourse")
    Map<Long, Integer> countEnrollCourseOfStudent(@RequestParam("studentIds") List<Long> studentIds);

    /**
     * Check whether current user has enrolled in a specified course
     * @param id Course ID
     * @return whether enrolled
     */
    @GetMapping("/order-details/course/{id}")
    Boolean checkMyLesson(@PathVariable("id") Long id);

    /**
     * Statistics on course purchase and refund status
     * @param courseId course id
     * @return statistical result
     */
    @GetMapping("/order-details/purchaseInfo")
    CoursePurchaseInfoDTO getPurchaseInfoOfCourse(@RequestParam("courseId") Long courseId);
}
