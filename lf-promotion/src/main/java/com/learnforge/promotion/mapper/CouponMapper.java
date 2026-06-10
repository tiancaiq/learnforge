package com.learnforge.promotion.mapper;

import com.learnforge.promotion.domain.po.Coupon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-02
 */
public interface CouponMapper extends BaseMapper<Coupon> {
    @Update("UPDATE coupon SET issue_num = issue_num + 1 WHERE id = #{couponId} AND issue_num < total_num")
    int incrIssuNum(@Param("couponId") Long couponId);
}
