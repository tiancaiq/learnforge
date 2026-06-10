package com.learnforge.promotion.service;

import com.learnforge.common.domain.dto.PageDTO;
import com.learnforge.promotion.domain.dto.CouponFormDTO;
import com.learnforge.promotion.domain.dto.CouponIssueFormDTO;
import com.learnforge.promotion.domain.po.Coupon;
import com.baomidou.mybatisplus.extension.service.IService;
import com.learnforge.promotion.domain.query.CouponQuery;
import com.learnforge.promotion.domain.vo.CouponPageVO;
import com.learnforge.promotion.domain.vo.CouponVO;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-02
 */
public interface ICouponService extends IService<Coupon> {

    void saveCoupon(@Valid CouponFormDTO dto);

    PageDTO<CouponPageVO> queryCouponByPage(CouponQuery query);

    void beginIusse(@Valid CouponIssueFormDTO dto);

    List<CouponVO> queryIssuingCoupons();

    void pauseIssue(Long id);
}
