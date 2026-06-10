package com.learnforge.promotion.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.learnforge.api.dto.promotion.CouponDiscountDTO;
import com.learnforge.api.dto.promotion.OrderCourseDTO;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.UserContext;
import com.learnforge.promotion.domain.po.Coupon;
import com.learnforge.promotion.domain.po.CouponScope;
import com.learnforge.promotion.mapper.UserCouponMapper;
import com.learnforge.promotion.service.ICouponScopeService;
import com.learnforge.promotion.service.IDiscountService;
import com.learnforge.promotion.strategy.discount.Discount;
import com.learnforge.promotion.strategy.discount.DiscountStrategy;
import com.learnforge.promotion.utils.PermuteUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class DiscountServiceImpl implements IDiscountService {

    private final UserCouponMapper userCouponMapper;

    private final ICouponScopeService scopeService;

    private final Executor discountSolutionExecutor;
    @Override
    public List<CouponDiscountDTO> findDiscountSolution(List<OrderCourseDTO> orderCourses) {
        if (CollUtil.isEmpty(orderCourses)) {
            return CollUtils.emptyList();
        }
        // query usable coupon
        List<Coupon> coupons =  userCouponMapper.queryMyCoupons(UserContext.getUser());
        if (CollUtil.isEmpty(coupons)) {
            return CollUtils.emptyList();
        }
        // preselect
        // get total
        int totalAmount = orderCourses.stream().mapToInt(OrderCourseDTO::getPrice).sum();

        List<Coupon> availableCoupons = coupons.stream()
                .filter(c -> DiscountStrategy.getDiscount(c.getDiscountType()).canUse(totalAmount, c))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(availableCoupons)) {
            return CollUtils.emptyList();
        }
        //3. list of combation
        //3.1 select every useable course,
        Map<Coupon, List<OrderCourseDTO>> availableCouponMap = findAvailableCoupon(availableCoupons,orderCourses);
        if (CollUtil.isEmpty(availableCouponMap)) {
            return CollUtils.emptyList();
        }
        // order
        availableCoupons = new ArrayList<>(availableCouponMap.keySet());

        List<List<Coupon>> solutions = PermuteUtil.permuteSubsets(availableCoupons);

        //4.calculate discount
        List<CompletableFuture<CouponDiscountDTO>> futures = new ArrayList<>(solutions.size());
        for (List<Coupon> solution : solutions) {
            futures.add(CompletableFuture.supplyAsync(
                    () -> calculateSolutionDiscount(availableCouponMap, orderCourses, solution),
                    discountSolutionExecutor));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        List<CouponDiscountDTO> list = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return findBestSolution(list);
    }

    private List<CouponDiscountDTO> findBestSolution(List<CouponDiscountDTO> list) {

        Map<String, CouponDiscountDTO> moreDiscountMap = new HashMap<>();
        Map<Integer, CouponDiscountDTO> lessCouponMap = new HashMap<>();

        //for the best
        for (CouponDiscountDTO solution : list) {

            //calute id combination
            String ids = solution.getIds().stream().sorted(Long::compare).map(String::valueOf).collect(Collectors.joining(","));

            //same coupon compare amount
            CouponDiscountDTO best = moreDiscountMap.get(ids);
            if (best != null && best.getDiscountAmount() >= solution.getDiscountAmount()) {
                continue;
            }

            // same amount compare coupon size
            best = lessCouponMap.get(solution.getDiscountAmount());
            if (best != null && best.getIds().size() <= solution.getIds().size()) {
                continue;
            }

            moreDiscountMap.put(ids, solution);
            lessCouponMap.put(solution.getDiscountAmount(), solution);
        }
        Collection<CouponDiscountDTO> bestSolutions = CollUtils.intersection(moreDiscountMap.values(), lessCouponMap.values());

        return bestSolutions.stream().sorted(Comparator.comparingInt(CouponDiscountDTO::getDiscountAmount).reversed()).collect(Collectors.toList());
    }

    private CouponDiscountDTO calculateSolutionDiscount(
            Map<Coupon, List<OrderCourseDTO>> couponMap, List<OrderCourseDTO> courses, List<Coupon> solution) {
        CouponDiscountDTO dto = new CouponDiscountDTO();
        // initial discount detail

        Map<Long, Integer> detailMap = courses.stream().collect(Collectors.toMap(OrderCourseDTO::getId, o -> 0));
        dto.setDiscountDetail(detailMap);

        //3, cal discount

        for (Coupon coupon : solution) {

            // get target course
            List<OrderCourseDTO> availableCourses = couponMap.get(coupon);
            // calcuate total course price

            int totalAmount = availableCourses.stream()
                    .mapToInt(oc -> oc.getPrice() -detailMap.get(oc.getId())).sum();
            if (totalAmount <= 0) {
                continue;
            }
            // check usable
            Discount discount = DiscountStrategy.getDiscount(coupon.getDiscountType());
            if (!discount.canUse(totalAmount, coupon)) {
                continue;

            }
            // discount amount
            int discountAmount = discount.calculateDiscount(totalAmount, coupon);
            // discount detail
            calculateDiscountDetails(detailMap,availableCourses,totalAmount,discountAmount);
            //dto
            dto.getIds().add(coupon.getCreater());
            dto.getRules().add(discount.getRule(coupon));
            dto.setDiscountAmount(discountAmount + dto.getDiscountAmount());

        }

        return dto;
    }

    private void calculateDiscountDetails(Map<Long, Integer> detailMap, List<OrderCourseDTO> courses,
                                          int totalAmount, int discountAmount) {

        int times =0;
        int remainDiscount = discountAmount;
        for (OrderCourseDTO course : courses) {
            // discount detail
             times ++;
             int discount =0;
             if (times == courses.size()) {
                 discount = remainDiscount;
             }else {
                 int payableAmount = course.getPrice() - detailMap.get(course.getId());
                 discount = discountAmount * payableAmount / totalAmount;

                 remainDiscount -= discount;
             }
            detailMap.put(course.getId(), discount + detailMap.get(course.getId()));
        }
    }

    private Map<Coupon, List<OrderCourseDTO>> findAvailableCoupon(List<Coupon> coupons, List<OrderCourseDTO> courses) {
        Map<Coupon, List<OrderCourseDTO>> map = new HashMap<>(coupons.size());
        for (Coupon coupon : coupons) {
            List<OrderCourseDTO> availableCourses = courses;
            if(coupon.getSpecific()){
                List<CouponScope> scopes = scopeService.lambdaQuery().eq(CouponScope::getCouponId, coupon.getId()).list();

                // get cate id in scope
                Set<Long> scopeIds = scopes.stream().map(CouponScope::getBizId).collect(Collectors.toSet());
                // courses

                availableCourses = courses.stream()
                        .filter(c -> scopeIds.contains(c.getCateId())).collect(Collectors.toList());
            }
            if (CollUtil.isEmpty(availableCourses)) {
                continue;
            }

            int totalAmount = availableCourses.stream().mapToInt(OrderCourseDTO::getPrice).sum();


            Discount discount = DiscountStrategy.getDiscount(coupon.getDiscountType());
            if(discount.canUse(totalAmount,coupon)){
                map.put(coupon,availableCourses);
            }
        }
        return map;
    }
}
