package com.learnforge.api.client.promotion;


import com.learnforge.api.client.promotion.fallback.PromotionClientFallback;
import com.learnforge.api.dto.promotion.CouponDiscountDTO;
import com.learnforge.api.dto.promotion.OrderCourseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(value = "promotion-service", fallbackFactory = PromotionClientFallback.class)
public interface PromotionClient {
    @PostMapping("/user-coupons/avaiable")
    List<CouponDiscountDTO> findDiscountSolution(@RequestBody List<OrderCourseDTO> orderCourses);
}
