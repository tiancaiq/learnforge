package com.learnforge.search.enums;

import lombok.Getter;

@Getter
public enum CourseStatus {
    NOT_READY(1, "Pending publication"),
    ON_THE_MARKET(2, "Published"),
    NO_LONGER_BE_SOLD(3, "Offline"),
    EXPIRED(4, "Completed");
    ;
    int value;
    String desc;

    CourseStatus(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
