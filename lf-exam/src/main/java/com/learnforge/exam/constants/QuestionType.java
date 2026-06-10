package com.learnforge.exam.constants;

import com.learnforge.common.enums.BaseEnum;
import lombok.Getter;

@Getter
public enum QuestionType implements BaseEnum {
    // 1: Single choice question, 2: Multiple choice question, 3: Unspecified choice question, 4: Judgment question, 5: Subjective question
    RADIO(1, "Single choice question"),
    MULTI(2, "Multiple choice question"),
    UNCERTAINTY(3, "Indefinite Choice Question"),
    JUDGE(4, "Judgment question"),
    SUBJECTIVE(5, "Subjective question"),
    ;
    int value;
    String desc;

    QuestionType(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public static QuestionType of(Integer value){
        if (value == null) {
            return null;
        }
        for (QuestionType status : values()) {
            if (status.equalsValue(value)) {
                return status;
            }
        }
        return null;
    }
}
