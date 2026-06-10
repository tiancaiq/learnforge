package com.learnforge.pay.sdk.constants;

import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum PayType implements BaseEnum {
    JSAPI(1, "Web payment JS"),
    MINI_APP(2, "Mini program payment"),
    APP(3, "APP payment"),
    NATIVE(4, "Scan code payment"),
    ;
    private final int value;
    private final String desc;

    PayType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
