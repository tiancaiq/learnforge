package com.learnforge.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.learnforge.common.constants.ErrorInfo;
import com.learnforge.common.exceptions.BadRequestException;
import lombok.Getter;

@Getter
public enum UserType implements BaseEnum{
    STAFF(1, "Other staff"),
    STUDENT(2, "Student"),
    TEACHER(3, "Teacher"),
    ;
    @EnumValue
    int value;
    String desc;

    UserType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static UserType of(int value) {
        for (UserType type : UserType.values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        throw new BadRequestException(ErrorInfo.Msg.INVALID_USER_TYPE);
    }
}
