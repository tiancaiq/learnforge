package com.learnforge.learning.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum SectionType implements BaseEnum {
    VIDEO(1, "Video"),
    EXAM(2, "Exam"),
    ;
    @JsonValue
    @EnumValue
    int value;
    String desc;

    SectionType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static SectionType of(Integer value){
        if (value == null) {
            return null;
        }
        for (SectionType status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}
