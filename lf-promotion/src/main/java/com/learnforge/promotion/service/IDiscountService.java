package com.learnforge.promotion.service;

import com.learnforge.api.dto.promotion.CouponDiscountDTO;
import com.learnforge.api.dto.promotion.OrderCourseDTO;

import java.util.List;

public interface IDiscountService {
    List<CouponDiscountDTO> findDiscountSolution(List<OrderCourseDTO> orderCourses);
}
