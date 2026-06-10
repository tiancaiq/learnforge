package com.learnforge.promotion.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.learnforge.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CouponStatus implements BaseEnum {
    DRAFT(1, "Draft"),
    UN_ISSUE(2, "Not started"),
    ISSUING(3, "Issuing"),
    FINISHED(4, "Finished"),
    PAUSE(5, "Paused");
    @JsonValue
    @EnumValue
    private final int value;
    private final String desc;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static CouponStatus of(Integer value) {
        if (value == null) {
            return null;
        }
        for (CouponStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        return null;
    }

    public static String desc(Integer value) {
        CouponStatus status = of(value);
        return status == null ? "" : status.desc;
    }
}
