package com.learnforge.api.client.promotion.fallback;

import com.learnforge.api.client.promotion.PromotionClient;
import com.learnforge.api.dto.promotion.CouponDiscountDTO;
import com.learnforge.api.dto.promotion.OrderCourseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collections;
import java.util.List;
@Slf4j
public class PromotionClientFallback implements FallbackFactory<PromotionClient> {
    @Override
    public PromotionClient create(Throwable cause) {
        log.error("query coupon fail", cause);
        return new PromotionClient() {
            @Override
            public List<CouponDiscountDTO> findDiscountSolution(List<OrderCourseDTO> orderCourses) {
                return Collections.emptyList();
            }
        };
    }
}
