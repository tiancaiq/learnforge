package com.learnforge.pay.third.model;

import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum RefundStatus implements BaseEnum {
    NOT_COMMIT(0, "Refund Request Not Submitted"),
    UN_KNOWN(1, "Unknown, Possibly Failed or Incomplete"),
    SUCCESS(2, "Refund Successful"),
    FAILED(3, "Refund Failed"),
    ;
    private final int value;
    private final String desc;

    RefundStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
