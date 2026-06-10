package com.learnforge.message.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TemplateStatus {
    DRAFT(0, "Draft"),
    IN_SERVICE(1, "In use"),
    OUT_OF_SERVICE(2, "Disabled"),
    ;

    private final int value;
    private final String desc;
}
