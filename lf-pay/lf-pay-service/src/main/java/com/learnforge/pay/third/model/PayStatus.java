package com.learnforge.pay.third.model;

import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum PayStatus implements BaseEnum {
    NOT_COMMIT(0, "Not Submitted"),
    WAIT_BUYER_PAY(1, "Pending Payment"),
    TRADE_CLOSED(2, "Closed"),
    TRADE_SUCCESS(3, "Payment Success"),
    TRADE_FINISHED(3, "Payment Success"),
    ;
    private final int value;
    private final String desc;

    PayStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
