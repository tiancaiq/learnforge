package com.learnforge.message.domain.enums;

import lombok.Getter;

@Getter
public enum SmsTemplate {
    VERIFY_CODE("SMS verification code"),
    ;
    private String desc;

    SmsTemplate( String desc) {
        this.desc = desc;
    }
}
