package com.learnforge.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NoticeType {
    SYSTEM(0, "System notification"),
    NOTE(1, "Note notification"),
    QA(2, "Question and answer notification"),
    OTHER(3, "Other notification"),
    PRIVATE_MESSAGE(4, "Private message"),
    ;

    private final int value;
    private final String desc;
}
