package com.learnforge.learning.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum PointsRecordType implements BaseEnum {
    LEARNING(1, "Course learning", 50),
    SIGN(2, "Daily check-in", 0),
    QA(3, "Course Q&A", 20),
    NOTE(4, "Course notes", 20),
    COMMENT(5, "Course review", 0),
    ;
    @EnumValue
    @JsonValue
    int value;
    String desc;
    int maxPoints;

    PointsRecordType(int value, String desc, int maxPoints) {
        this.value = value;
        this.desc = desc;
        this.maxPoints = maxPoints;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static PointsRecordType of(Integer value){
        if (value == null) {
            return null;
        }
        for (PointsRecordType status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}