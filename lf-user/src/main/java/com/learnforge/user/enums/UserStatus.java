package com.learnforge.user.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.learnforge.common.exceptions.BadRequestException;
import com.learnforge.user.constants.UserErrorInfo;
import lombok.Getter;

@Getter
public enum UserStatus {
    FROZEN(0, "Prohibited use"),
    NORMAL(1, "Activated"),
    ;
    @EnumValue
    int value;
    String desc;

    UserStatus(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserStatus of(int value) {
        if (value == 0) {
            return FROZEN;
        }
        if (value == 1) {
            return NORMAL;
        }
        throw new BadRequestException(UserErrorInfo.Msg.INVALID_USER_STATUS);
    }
}