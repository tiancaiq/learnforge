package com.learnforge.learning.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum LessonStatus implements BaseEnum {
    NOT_BEGIN(0, "Not started"),
    LEARNING(1, "In progress"),
    FINISHED(2, "Completed"),
    EXPIRED(3, "Expired"),
    ;
    @JsonValue
    @EnumValue
    int value;
    String desc;

    LessonStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static LessonStatus of(Integer value){
        if (value == null) {
            return null;
        }
        for (LessonStatus status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}
