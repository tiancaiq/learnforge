package com.learnforge.promotion.strategy.discount;

import com.learnforge.common.utils.NumberUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.promotion.domain.po.Coupon;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PerPriceDiscount implements Discount {

    private final static String RULE_TEMPLATE = "Save {} for every {} spent, up to {}";

    @Override
    public boolean canUse(int totalAmount, Coupon coupon) {
        return totalAmount >= coupon.getThresholdAmount();
    }

    @Override
    public int calculateDiscount(int totalAmount, Coupon coupon) {
        int discount = 0;
        Integer thresholdAmount = coupon.getThresholdAmount();
        Integer discountValue = coupon.getDiscountValue();
        while (totalAmount >= thresholdAmount) {
            discount += discountValue;
            totalAmount -= thresholdAmount;
        }
        return Math.min(discount, coupon.getMaxDiscountAmount());
    }

    @Override
    public String getRule(Coupon coupon) {
        return StringUtils.format(
                RULE_TEMPLATE,
                NumberUtils.scaleToStr(coupon.getDiscountValue(), 2),
                NumberUtils.scaleToStr(coupon.getThresholdAmount(), 2),
                NumberUtils.scaleToStr(coupon.getMaxDiscountAmount(), 2));
    }
}
