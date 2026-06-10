package com.learnforge.common.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.collection.IterUtil;
import com.learnforge.common.validate.Checker;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Collection utility class inherited from hutool
 */
public class CollUtils extends CollectionUtil {

    public static <T> List<T> emptyList() {
        return Collections.emptyList();
    }

    public static <T> Set<T> emptySet() {
        return Collections.emptySet();
    }

    public static <K,V> Map<K, V> emptyMap() {
        return Collections.emptyMap();
    }

    public static <T> Set<T> singletonSet(T t) {
        return Collections.singleton(t);
    }

    public static <T> List<T> singletonList(T t) {
        return Collections.singletonList(t);
    }

    public static List<Integer> convertToInteger(List<String> originList){
        return CollUtils.isNotEmpty(originList) ? originList.stream().map(NumberUtils::parseInt).collect(Collectors.toList()) : null;
    }

    public static List<Long> convertToLong(List<String> originLIst){
        return CollUtils.isNotEmpty(originLIst) ? originLIst.stream().map(NumberUtils::parseLong).collect(Collectors.toList()) : null;
    }

    /**
     * Convert collection to string with conjunction as delimiter. If collection elements are arrays, Iterable, or Iterator, recursively combine them into a string
     * @param collection Collection
     * @param conjunction Delimiter
     * @param <T> Collection element type
     * @return Concatenated string
     * See Also: IterUtil.join(Iterator, CharSequence)
     */
    public static <T> String join(Collection<T> collection, CharSequence conjunction) {
        if (null == collection || collection.isEmpty()) {
            return null;
        }
        return IterUtil.join(collection.iterator(), conjunction);
    }

    public static <T> String joinIgnoreNull(Collection<T> collection, CharSequence conjunction) {
        if (null == collection || collection.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (T t : collection) {
            if(t == null) continue;
            sb.append(t).append(",");
        }
        if(sb.length() <= 0){
            return null;
        }
        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    /**
     * Collection validation logic
     *
     * @param data Collection to validate
     * @param checker Validator
     * @param <T> Collection element type
     */
    public static  <T> void  check(List<T> data, Checker<T> checker){
        if(data == null){
            return;
        }
        for (T t : data){
            checker.check(t);
        }
    }

    /**
     * Collection validation logic
     *
     * @param data Collection to validate
     * @param <T> Collection element type
     */
    public static  <T extends Checker<T>> void  check(List<T> data){
        if(data == null){
            return;
        }
        for (T t : data){
            t.check();
        }
    }

    /**
     * Add element to collection, filter out null
     *
     * @param list Collection
     * @param data Data to add
     * @param <T> Element type
     */
    public static <T> void add(Collection<T> list, T... data) {
        if (list == null || ArrayUtils.isEmpty(data)) {
            return;
        }
        for (T t : data) {
            if (ObjectUtils.isNotEmpty(t)) {
                list.add(t);
            }
        }
    }
    //Add occurrence counts of two collections
    public static Map<Long, Integer> union(Map<Long, Integer> map1, Map<Long, Integer> map2) {
        if (CollUtils.isEmpty(map1)) {
            return map2;
        } else if (CollUtils.isEmpty(map2)) {
            return map1;
        }
        for (Map.Entry<Long, Integer> entry : map1.entrySet()) {
            Integer num = map2.get(entry.getKey());
            map2.put(entry.getKey(), NumberUtils.null2Zero(num) + entry.getValue());
        }
        return map2;
    }

    public static <T,R> R getFiledOfFirst(List<T> list, Function<T, R> function) {
        if (CollUtils.isEmpty(list)) {
            return null;
        }
        return function.apply(list.get(0));
    }
}