package com.learnforge.pay.sdk.constants;

import com.learnforge.common.utils.StringUtils;
import lombok.Getter;

@Getter
public enum PayChannel {
    wxPay("WeChat Pay"),
    aliPay("Alipay Pay"),
    ;

    private final String desc;

    PayChannel(String desc) {
        this.desc = desc;
    }

    public static String desc(String value){
        if (StringUtils.isBlank(value)) {
            return "";
        }
        return PayChannel.valueOf(value).getDesc();
    }
}
