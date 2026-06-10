package com.learnforge.promotion.controller;


import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.promotion.domain.dto.CouponFormDTO;
import com.learnforge.promotion.domain.dto.CouponIssueFormDTO;
import com.learnforge.promotion.domain.po.Coupon;
import com.learnforge.promotion.domain.query.CouponQuery;
import com.learnforge.promotion.domain.vo.CouponPageVO;
import com.learnforge.promotion.domain.vo.CouponVO;
import com.learnforge.promotion.service.ICouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/coupons")
@Api(tags ="coupon related")
public class CouponController {


    private final ICouponService couponService;
    @ApiOperation("new coupon")
    @PostMapping
    public void saveCoupon(@RequestBody @Valid CouponFormDTO dto){
        couponService.saveCoupon(dto);
    }
    @ApiOperation("query coupon")
    @GetMapping("/page")
    public PageDTO<CouponPageVO> queryCouponByPage(CouponQuery query){
        return couponService.queryCouponByPage(query);

    }
    @ApiOperation("issue coupon")
    @PutMapping("/{id}/issue")
    public void beginIssue(@RequestBody @Valid CouponIssueFormDTO dto){
        couponService.beginIusse(dto);
    }

    @ApiOperation("query issuing coupons")
    @GetMapping("/list")
    public List<CouponVO> queryIssuingCoupons(){
        return couponService.queryIssuingCoupons();
    }

    @ApiOperation("Pause Issue")
    @PutMapping("/{id}/pause")
    public void pauseIssue(@PathVariable Long id) {
        couponService.pauseIssue(id);
    }
}
