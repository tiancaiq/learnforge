package com.learnforge.common.utils;

import cn.hutool.core.bean.BeanUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Inherits from hutool's BeanUtil, adds the function of custom converter when converting beans
 */
public class BeanUtils extends BeanUtil {

    /**
     * Convert original object to target object, use converter to handle mismatched fields
     *
     * @param source  Original object
     * @param clazz   Target object's class
     * @param convert Converter
     * @param <R>     Original object type
     * @param <T>     Target object type
     * @return Target object
     */
    public static <R, T> T copyBean(R source, Class<T> clazz, Convert<R, T> convert) {
        T target = copyBean(source, clazz);
        if (convert != null) {
            convert.convert(source, target);
        }
        return target;
    }
    /**
     * Convert original object to target object, use converter to handle mismatched fields
     *
     * @param source  Original object
     * @param clazz   Target object's class
     * @param <R>     Original object type
     * @param <T>     Target object type
     * @return Target object
     */
    public static <R, T> T copyBean(R source, Class<T> clazz){
        if (source == null) {
            return null;
        }
        return toBean(source, clazz);
    }

    public static <R, T> List<T> copyList(List<R> list, Class<T> clazz) {
        if (list == null || list.size() == 0) {
            return CollUtils.emptyList();
        }
        return copyToList(list, clazz);
    }

    public static <R, T> List<T> copyList(List<R> list, Class<T> clazz, Convert<R, T> convert) {
        if (list == null || list.size() == 0) {
            return CollUtils.emptyList();
        }
        return list.stream().map(r -> copyBean(r, clazz, convert)).collect(Collectors.toList());
    }
}