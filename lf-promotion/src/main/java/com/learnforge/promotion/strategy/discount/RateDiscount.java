package com.learnforge.promotion.strategy.discount;

import com.learnforge.common.utils.NumberUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.promotion.domain.po.Coupon;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class RateDiscount implements Discount {

    private static final String RULE_TEMPLATE =
            "Spend at least {}, pay {}% of the original price, maximum discount {}";

    @Override
    public boolean canUse(int totalAmount, Coupon coupon) {
        return totalAmount >= coupon.getThresholdAmount();
    }

    @Override
    public int calculateDiscount(int totalAmount,  Coupon coupon) {

        return Math.min(coupon.getMaxDiscountAmount(), totalAmount * (100 - coupon.getDiscountValue()) / 100);
    }

    @Override
    public String getRule( Coupon coupon) {
        return StringUtils.format(
                RULE_TEMPLATE,
                NumberUtils.scaleToStr(coupon.getThresholdAmount(), 2),
                String.valueOf(coupon.getDiscountValue()),
                NumberUtils.scaleToStr(coupon.getMaxDiscountAmount(), 2)
        );
    }
}
