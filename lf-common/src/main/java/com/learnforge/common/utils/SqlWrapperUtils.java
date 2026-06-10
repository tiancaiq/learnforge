package com.learnforge.common.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;

import java.lang.reflect.Field;

/**
 * Convert the query condition object and modified query conditions to QueryWrapper
 * Query wrapper {@link LambdaQueryWrapper}
 * Update wrapper {@link LambdaUpdateWrapper}
 *
 * @ClassName SqlWrapperUtils
 * @author wusongsong
 * @since 2022/7/14 11:43
 * @version 1.0.0
 **/
public class SqlWrapperUtils {


    /**
     * Used to generate query statement columns
     *
     * @param clazz
     * @param prefix
     * @return
     */
    public static String getSqlCoumns(Class<?> clazz, String prefix) {

        Field[] fields = ReflectUtils.getFields(clazz);
        if (ArrayUtils.isEmpty(fields)) {
            //If the model has no fields, provide an empty value to prevent further queries
            return null;
        }
        if (StringUtils.isNotEmpty(prefix)) {
            prefix = prefix + ".";
        }
        StringBuilder buffer = new StringBuilder();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (ReflectUtils.getMethod(clazz, "get" + StringUtils.upperFirst(fieldName)) != null) {
                buffer.append(prefix).append(StringUtils.toUnderlineCase(fieldName))
                        .append(",");
            }
        }
        return buffer.substring(0, buffer.length() - 1);
    }

    /**
     * Convert the query DTO to LambdaQueryWrapper, currently supports equal queries
     *
     * @param queryDTO SQL query condition DTO
     * @param targetClazz Class corresponding to the database PO for the query
     * @param <T> Type of the database PO for the query
     * @param <R> Type of the condition object to be converted
     * @return LambdaQueryWrapper object
     */
    public static <T, R> LambdaQueryWrapper<T> toLambdaQueryWrapper(R queryDTO, Class<T> targetClazz) {
        //SQL query conditions
        LambdaQueryWrapper<T> queryWrapper = new LambdaQueryWrapper<>();
        T target = BeanUtils.toBean(queryDTO, targetClazz);
        queryWrapper.setEntity(target);
        return queryWrapper;
    }

    /**
     * Convert the query DTO to QueryWrapper, currently supports equal queries
     *
     * @param queryDTO SQL query condition DTO
     * @param targetClazz Class corresponding to the database PO for the query
     * @param <T> Type of the database PO for the query
     * @param <R> Type of the condition object to be converted
     * @return QueryWrapper object
     */
    public static <T, R> QueryWrapper<T> toQueryWrapper(R queryDTO, Class<T> targetClazz) {
        //SQL query conditions
        QueryWrapper<T> queryWrapper = new QueryWrapper<>();
        T target = BeanUtils.toBean(queryDTO, targetClazz);
        queryWrapper.setEntity(target);
        return queryWrapper;
    }

    /**
     * Used for join table pagination query, to supplement fields from the second table
     *
     * @param queryWrapper QueryWrapper
     * @param prefix Prefix for query conditions
     * @param queryDTO Query entity
     * @param targetClasszz Target class
     * @param <T> Target type
     * @param <R> Query entity type
     */
    public static <T, R> void suppleQueryWrapper(QueryWrapper<?> queryWrapper, String prefix, R queryDTO, Class<T> targetClasszz) {
        //Convert the query DTO to the target object
        T target = BeanUtils.toBean(queryDTO, targetClasszz);
        //Get the fields of the target object
        Field[] fields = ReflectUtils.getFields(targetClasszz);
        if (ArrayUtils.isNotEmpty(fields)) {
            //Traverse all fields
            for (Field field : fields) {
                //Get field value
                Object value = ReflectUtils.getFieldValue(target, field);
                //If the field has a value and has a get method (ensure the field corresponds to a database field), add the field to the query statement
                if (value != null && ReflectUtils.getMethod(targetClasszz, "get" + StringUtils.upperFirst(field.getName())) != null) {
                    queryWrapper.eq(prefix + "." + StringUtils.toUnderlineCase(field.getName()), value);
                }
            }
        }
    }

    /**
     * Convert the update DTO to LambdaUpdateWrapper, currently supports equal queries. If using in or date conversion, use convert processing
     *
     * @param updateDTO SQL update condition DTO
     * @param targetClazz Class corresponding to the database PO for the update
     * @param <T> Type of the database PO for the update
     * @param <R> Type of the condition object to be converted
     * @return LambdaQueryWrapper object
     */
    public static <T, R> LambdaUpdateWrapper<T> toLambdaUpdateWrapper(R updateDTO, Class<T> targetClazz) {
        //SQL query conditions
        LambdaUpdateWrapper<T> updateWrapper = new LambdaUpdateWrapper<>();
        T target = BeanUtils.toBean(updateDTO, targetClazz);
        updateWrapper.setEntity(target);
        return updateWrapper;
    }

}
