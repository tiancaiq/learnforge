package com.learnforge.promotion.mapper;

import com.learnforge.promotion.domain.po.Coupon;
import com.learnforge.promotion.domain.po.UserCoupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-07
 */
public interface UserCouponMapper extends BaseMapper<UserCoupon> {

    List<Coupon> queryMyCoupons(@Param("userId") Long userId);
}
