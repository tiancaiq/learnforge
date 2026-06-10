package com.learnforge.promotion.strategy.discount;

import com.learnforge.common.utils.NumberUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.promotion.domain.po.Coupon;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PriceDiscount implements Discount{

    private static final String RULE_TEMPLATE = "Spend at least {}, save {}";


    @Override
    public boolean canUse(int totalAmount, Coupon coupon) {
        return totalAmount >= coupon.getThresholdAmount();
    }

    @Override
    public int calculateDiscount(int totalAmount, Coupon coupon) {
        return coupon.getDiscountValue();
    }

    @Override
    public String getRule(Coupon coupon) {
        return StringUtils.format(
                RULE_TEMPLATE,
                NumberUtils.scaleToStr(coupon.getThresholdAmount(), 2),
                NumberUtils.scaleToStr(coupon.getDiscountValue(), 2)
        );
    }
}
