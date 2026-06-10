package com.learnforge.promotion.controller;


import com.learnforge.api.dto.promotion.CouponDiscountDTO;
import com.learnforge.api.dto.promotion.OrderCourseDTO;
import com.learnforge.promotion.service.IDiscountService;
import com.learnforge.promotion.service.IUserCouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-07
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user-coupons")
@Api(tags = "coupons related")
public class UserCouponController {


    private final IUserCouponService userCouponService;
    private final IDiscountService discountService;

    @ApiOperation("get coupon")
    @PostMapping("/{couponId}/receive")
    public void receiveCoupon(@PathVariable("couponId") Long couponId) {
        userCouponService.receiveCoupon(couponId);
    }

    @ApiOperation("redeem coupon")
    @PostMapping("/{code}/exchange")
    public void exchangeCoupon(@PathVariable("code") String  code) {
        userCouponService.exchangeCoupon(code);
    }


    @PostMapping("/avaiable")
    public List<CouponDiscountDTO> findDiscountSolution(@RequestBody List<OrderCourseDTO> orderCourses){
        return discountService.findDiscountSolution(orderCourses);
    }
}
