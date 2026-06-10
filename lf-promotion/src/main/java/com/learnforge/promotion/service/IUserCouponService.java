package com.learnforge.promotion.service;

import com.learnforge.promotion.domain.dto.UserCouponDTO;
import com.learnforge.promotion.domain.po.Coupon;
import com.learnforge.promotion.domain.po.UserCoupon;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>

 * </p>
 *
 * @author luke
 * @since 2026-06-07
 */
public interface IUserCouponService extends IService<UserCoupon> {

    void receiveCoupon(Long couponId);


    void checkAndCreateUserCoupon(UserCouponDTO uc);

    void exchangeCoupon(String code);
}
