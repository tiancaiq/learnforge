package com.learnforge.pay.sdk.constants;

import com.learnforge.common.utils.StringUtils;
import lombok.Getter;

@Getter
public enum RefundChannelEnum {
    COUPON("Alipay red envelope"),
    ALIPAYACCOUNT("Alipay account"),
    POINT("Jifubao"),
    DISCOUNT("Discount coupon"),
    PCARD("Prepaid card"),
    MCARD("Merchant stored value card"),
    MDISCOUNT("Merchant coupon"),
    MCOUPON("Merchant red envelope"),
    PCREDIT("Ant Huabei"),
    BANKCARD("Bank card"),
    MONEYFUND("Balance Treasure"),
    VOUCHER("Coupon"),
    ORIGINAL("Refund to original path"),
    BALANCE("Refund to balance"),
    OTHER_BALANCE("Other balance accounts"),
    OTHER_BANKCARD("Other bank cards"),
    ;
    private final String desc;

    RefundChannelEnum(String desc) {
        this.desc = desc;
    }

    public static String desc(String value){
        if(StringUtils.isBlank(value)){
            return "";
        }
        return RefundChannelEnum.valueOf(value).getDesc();
    }
}
