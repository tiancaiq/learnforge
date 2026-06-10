package com.learnforge.trade.controller;


import com.learnforge.api.dto.course.CoursePurchaseInfoDTO;
import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.trade.domain.query.OrderDetailPageQuery;
import com.learnforge.trade.domain.vo.OrderDetailAdminVO;
import com.learnforge.trade.domain.vo.OrderDetailPageVO;
import com.learnforge.trade.service.IOrderDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Order Detail Frontend Controller
 * </p>
 *
 * @author LearnForge contributors
 * @since 2022-08-29
 */

@Api(tags = "Order Detail Related Interfaces")
@RestController
@RequestMapping("/order-details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final IOrderDetailService detailService;

    @ApiOperation("Page Query Order Details")
    @GetMapping("/page")
    public PageDTO<OrderDetailPageVO> queryDetailForPage(OrderDetailPageQuery pageQuery) {
        return detailService.queryDetailForPage(pageQuery);
    }

    @ApiOperation("Get Order Detail by Order Detail ID")
    @GetMapping("/{id}")
    public OrderDetailAdminVO queryOrdersDetailProgress( @ApiParam(value = "Order Detail ID")@PathVariable("id") Long id) {
        return detailService.queryOrdersDetailProgress(id);
    }

    @ApiOperation("Check if Course is Purchased and Expired")
    @GetMapping("/course/{id}")
    public Boolean checkCourseOrderInfo(@PathVariable("id") Long courseId){
        return detailService.checkCourseOrderInfo(courseId);
    }

    @ApiOperation("Count Course Registration Numbers")
    @GetMapping("/enrollNum")
    public Map<Long, Integer> countEnrollNumOfCourse(@RequestParam("courseIdList") List<Long> courseIdList){
        return detailService.countEnrollNumOfCourse(courseIdList);
    }

    @ApiOperation("Count Student Course Registrations")
    @GetMapping("/enrollCourse")
    public Map<Long, Integer> countEnrollCourseOfStudent(@RequestParam("studentIds") List<Long> studentIds){
        return detailService.countEnrollCourseOfStudent(studentIds);
    }

    @GetMapping("purchaseInfo")
    public CoursePurchaseInfoDTO getPurchaseInfoOfCourse(@RequestParam("courseId") Long courseId){
        return detailService.getPurchaseInfoOfCourse(courseId);
    }
}
