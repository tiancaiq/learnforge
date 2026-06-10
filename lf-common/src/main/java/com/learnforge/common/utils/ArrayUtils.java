package com.learnforge.common.utils;

import cn.hutool.core.util.ArrayUtil;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Array utility class
 * @ClassName ArrayUtils
 * @author wusongsong
 * @since 2022/7/10 12:02
 * @version 1.0.0
 **/
public class ArrayUtils extends ArrayUtil {



    /**
     * Convert source array into list of specified type
     *
     * @param originList  Original list
     * @param targetClazz Target list element type
     * @param <R>         Original list element type
     * @param <T>         Target list element type
     * @return Collection of target type
     */
    public static <R, T> List<T> convert(R[] originList, Class<T> targetClazz) {
       return convert(originList, targetClazz, null);

    }

    /**
     * Convert source array into list of specified type
     *
     * @param originList  Original list
     * @param targetClazz Target list element type
     * @param convert     Conversion interface for special fields
     * @param <R>         Original list element type
     * @param <T>         Target list element type
     * @return Collection of target type
     */
    public static <R, T> List<T> convert(R[] originList, Class<T> targetClazz, Convert<R, T> convert) {
        if (isEmpty(originList)) {
            return null;
        }

        return Arrays.stream(originList)
                .map(origin -> BeanUtils.copyBean(origin, targetClazz, convert))
                .collect(Collectors.toList());

    }
}
