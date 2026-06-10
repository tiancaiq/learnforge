package com.learnforge.common.utils;

import cn.hutool.core.util.ReflectUtil;

/**
 * Reflection utility
 **/
public class ReflectUtils extends ReflectUtil {

    /**
     * Determine if a class contains a specified field
     *
     * @param fieldName Specified field name
     * @param clazz Class
     * @return Whether it contains true/false
     */
    public static boolean containField(String fieldName, Class<?> clazz) {
        return getField(clazz, fieldName) != null;
    }
}
