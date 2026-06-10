package com.learnforge.pay.constants;

import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum NotifyStatus implements BaseEnum {
    UN_CALL(0, "Callback not started"),
    CALLING(1, "Callback in progress"),
    SUCCESS(2, "Callback succeeded"),
    FAILED(3, "All callbacks have failed"),
    ;
    private final int value;
    private final String desc;

    NotifyStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
