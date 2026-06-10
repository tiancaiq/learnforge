package com.learnforge.common.constants;

import cn.hutool.core.lang.RegexPool;

public interface RegexConstants extends RegexPool {
    /**
     * Mobile phone regular expression
     */
    String PHONE_PATTERN = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$";
    /**
     * Email regular expression
     */
    String EMAIL_PATTERN = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    /**
     * Password regular expression. 6~32 characters of letters, numbers, and underscores
     */
    String PASSWORD_PATTERN = "^\\w{4,24}$";
    /**
     * Username regular expression. 6~32 characters of letters, numbers, and underscores
     */
    String USERNAME_PATTERN = "^\\w{4,32}$";
    /**
     * Verification code regular expression, 6-digit number or letters
     */
    String VERIFY_CODE_PATTERN = "^[a-zA-Z\\d]{6}$";

    /**
     * Coupon redemption code template
     */
    String COUPON_CODE_PATTERN = "^[23456789ABCDEFGHJKLMNPQRSTUVWXYZ]{8,10}$";
}
