package com.learnforge.promotion.strategy.discount;

import com.learnforge.promotion.domain.po.Coupon;

/**

 */
public interface Discount {
    /**


     */
    boolean canUse(int totalAmount, Coupon coupon);

    /**


     */
    int calculateDiscount(int totalAmount, Coupon coupon);

    /**


     */
    String getRule(Coupon coupon);
}
