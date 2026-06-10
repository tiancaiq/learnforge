package com.learnforge.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tree data processing tool
 *
 * @ClassName TreeDataUtils
 * @author wusongsong
 * @since 2022/7/19 14:31
 * @version 1.0.0
 **/
@Slf4j
public class TreeDataUtils {


    /**
     * Traverse tree data to calculate the target
     * @param data
     * @param calculateDataProcessor
     * @param <T>
     * @return
     */
    public static <T> Object ergodicTreeCalculate(List<T> data, CalculateDataProcessor<T> calculateDataProcessor) {
        if(CollUtils.isEmpty(data)){
            return null;
        }
        Object result = null;
        for (T t :data ) {
            Object tData = calculateDataProcessor.getData(t);
            List<T> childData = calculateDataProcessor.getChildData(t);
            Object currentAllData = calculateDataProcessor.calculate(tData, ergodicTreeCalculate(childData, calculateDataProcessor));
            calculateDataProcessor.setResult(t, currentAllData);
            result = calculateDataProcessor.calculate(result, currentAllData);
        }
        return result;
    }

    /**
     * Convert tree data to a list of target type and establish parent-child relationships, using recursion
     *
     * @param parentKey Parent data key
     * @param originData Original tree data
     * @param dataProcessor Data converter
     * @param clazz Target type
     * @param convert Converter to transform original data into target data
     * @param targetData Target data will be placed into this list, this field cannot be empty
     * @param <T> Target data type
     * @param <R> Original data type
     */
    public static <T, R> void parseTreeToList(Object parentKey, List<R> originData,
                                              ToListDataProcessor<T, R> dataProcessor, Class<T> clazz,
                                              Convert<R, T> convert, List<T> targetData, Filter<R> filter) {
        if (CollUtils.isNotEmpty(originData)) {
            for (R data : originData) {
                T target = BeanUtils.copyBean(data, clazz, convert);
                dataProcessor.setParent(target, parentKey);
                targetData.add(target);
                parseTreeToList(dataProcessor.getKey(data), dataProcessor.getChildren(data), dataProcessor, clazz, convert, targetData, filter);
            }
        }
    }

    /**
     * Convert original data list into tree data based on parent-child relationships, and convert data into target type
     * Applicable scenario: Each data item has a unique identifier and a unique identifier of its parent data
     *
     * @param originData Original data, list
     * @param clazz Target type class
     * @param dataProcessor Tree data wrapper
     * @param <T> Target data type
     * @param <R> Original data type
     * @return Tree data of target data type
     */
    public static <T, R> List<T> parseToTree(List<R> originData, Class<T> clazz, DataProcessor<T, R> dataProcessor) {
        return parseToTree(originData, clazz, null, dataProcessor, new DefaultFilter());
    }

    /**
     * Convert original data list into tree data based on parent-child relationships, and convert data into target type
     * Applicable scenario: Each data item has a unique identifier and a unique identifier of its parent data
     *
     * @param originData Original data, list
     * @param clazz Target type class
     * @param dataProcessor Tree data wrapper
     * @param <T> Target data type
     * @param <R> Original data type
     * @return Tree data of target data type
     */
    public static <T, R> List<T> parseToTree(List<R> originData, Class<T> clazz, DataProcessor<T, R> dataProcessor, Filter<R> filter) {
        return parseToTree(originData, clazz, null, dataProcessor, filter);
    }

    /**
     * Convert original data list into tree data based on parent-child relationships, and convert data into target type
     * Applicable scenario: Each data item has a unique identifier and a unique identifier of its parent data
     *
     * @param originData Original data, list
     * @param clazz Target type class
     * @param dataProcessor Tree data wrapper
     * @param <T> Target data type
     * @param <R> Original data type
     * @return Tree data of target data type
     */
    public static <T, R> List<T> parseToTree(List<R> originData, Class<T> clazz, Convert<R,T> convert, DataProcessor<T, R> dataProcessor) {
        return parseToTree(originData, clazz, convert, dataProcessor, new DefaultFilter());
    }


    /**
     * Convert original data list into tree data based on parent-child relationships, and convert data into target type
     *
     * @param originData Original data, list
     * @param clazz Target type class
     * @param convert Converter to transform original data into target data
     * @param dataProcessor Tree data wrapper
     * @param <T> Target data type
     * @param <R> Original data type
     * @return Tree data of target data type
     */
    public static <T, R> List<T> parseToTree(List<R> originData, Class<T> clazz, Convert<R, T> convert, DataProcessor<T, R> dataProcessor, Filter<R> filter) {
        //1. If original data is empty, return an empty list
        if (CollUtils.isEmpty(originData)) {
            return new ArrayList<>();
        }
        //2. Initialize a map to build tree relationships
        Map<Object, T> resultMap = new HashMap<>();
        //3. Traverse data
        originData.stream().forEach(r -> {
            if(!filter.filter(r)){
                return;
            }
            //3.1 Convert data to specified type
            T current = BeanUtils.copyBean(r, clazz, convert);
            //3.2 Initialize an empty list of child data for current data
            dataProcessor.setChild(current, new ArrayList<>());
            //3.3 Get unique key of current data
            Object key = dataProcessor.getKey(r);
            //3.4 Get current data from resultMap, mainly copy child data of already added data
            T currentInMap = resultMap.get(key);
            if (currentInMap != null) {
                //3.5 If current data already exists in resultMap, set its child list to newly generated target data
                List<T> children = dataProcessor.getChild(currentInMap);
                dataProcessor.setChild(current, children);
            }

            //3.6 Get parent data key of current data, if parent data does not exist, initialize an empty one
            Object parentKey = dataProcessor.getParentKey(r);
            T parent = resultMap.get(parentKey);
            //3.7 Initialize parent data
            if (parent == null) {
                parent = ReflectUtils.newInstance(clazz);
                dataProcessor.setChild(parent, new ArrayList<>());
            }
            //3.8 Add child data to parent data's child list
            List<T> children = dataProcessor.getChild(parent);
            children.add(current);
            //3.9 Put parent data into resultMap
            resultMap.put(parentKey, parent);
            //3.10 Put child data into resultMap
            resultMap.put(dataProcessor.getKey(r), current);
        });
        //4. Extract assembled data
        T t = resultMap.get(dataProcessor.getRootKey());
        return t== null ? null : dataProcessor.getChild(t);
    }

    /**
     * Tree data processor
     *
     * @param <T> Target data
     * @param <R> Original data
     */
    public interface DataProcessor<T, R> {
        /**
         * Get parent data key from current data, which represents the relationship with parent data
         *
         * @param r
         * @return
         */
        Object getParentKey(R r);

        /**
         * Get unique identifier of current data
         *
         * @param r
         * @return
         */
        Object getKey(R r);

        /**
         * Get root data of the entire tree
         *
         * @return
         */
        Object getRootKey();

        /**
         * Get child data list
         *
         * @param t
         * @return
         */
        List<T> getChild(T t);

        /**
         * Put child data list into parent data
         *
         * @param parent
         * @param child
         */
        void setChild(T parent, List<T> child);

    }

    public interface ToListDataProcessor<T, R> {
        Object getKey(R r);

        void setParent(T t, Object parentKey);

        List<R> getChildren(R r);
    }

    public interface CalculateDataProcessor<T> {

        Object getData(T t);

        List<T> getChildData(T t);

        Object calculate(Object ... datas);

        void setResult(T t, Object result);
    }

    /**
     * Filter
     * @param <T>
     */
    public interface Filter<T>{
        default boolean filter(T t){
            return true;
        }
    }

    public static class DefaultFilter<T> implements Filter{

    }
}