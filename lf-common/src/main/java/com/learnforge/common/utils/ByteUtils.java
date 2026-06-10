package com.learnforge.common.utils;

import cn.hutool.core.util.ByteUtil;

public class ByteUtils extends ByteUtil {

    /**
     * Convert byte[] array to string, return "" if null
     * @param content Byte content
     * @return String value
     */
    public static String parse(byte[] content){
        if(content == null || content.length <= 0) {
            return StringUtils.EMPTY;
        }
        return new String(content);
    }
}
