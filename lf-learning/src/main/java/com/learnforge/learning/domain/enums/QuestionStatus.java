package com.learnforge.learning.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum QuestionStatus implements BaseEnum {
    UN_CHECK(0, "Unreviewed"),
    CHECKED(1, "Reviewed"),
    ;
    @JsonValue
    @EnumValue
    int value;
    String desc;

    QuestionStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static QuestionStatus of(Integer value){
        if (value == null) {
            return null;
        }
        for (QuestionStatus status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}
