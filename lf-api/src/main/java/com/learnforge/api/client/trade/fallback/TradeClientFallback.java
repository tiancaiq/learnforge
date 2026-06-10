package com.learnforge.api.client.trade.fallback;

import com.learnforge.api.client.trade.TradeClient;
import com.learnforge.api.dto.course.CoursePurchaseInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TradeClientFallback implements FallbackFactory<TradeClient> {

    @Override
    public TradeClient create(Throwable cause) {
        log.error("Query transaction service exception", cause);
        return new TradeClient() {

            @Override
            public Map<Long, Integer> countEnrollNumOfCourse(List<Long> courseIdList) {
                return new HashMap<>();
            }

            @Override
            public Map<Long, Integer> countEnrollCourseOfStudent(List<Long> studentIds) {
                return new HashMap<>();
            }

            @Override
            public Boolean checkMyLesson(Long id) {
                return false;
            }

            @Override
            public CoursePurchaseInfoDTO getPurchaseInfoOfCourse(Long courseId) {
                return new CoursePurchaseInfoDTO();
            }
        };
    }
}
