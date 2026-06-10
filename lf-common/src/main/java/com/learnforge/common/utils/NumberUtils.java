package com.learnforge.common.utils;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class NumberUtils extends NumberUtil {


    /**
     * If number is null, convert number to 0, otherwise return original number
     *
     * @param number Original number
     * @return Integer number, 0 or original number
     */
    public static Integer null2Zero(Integer number){
        return number == null ? 0 : number;
    }

    /**
     * If number is null, convert number to 0, otherwise return original number
     *
     * @param number Original number
     * @return Integer number, 0 or original number
     */
    public static Double null2Zero(Double number){
        return number == null ? 0 : number;
    }

    /**
     * If number is null, convert number to 0L, otherwise return original number
     *
     * @param number Original number
     * @return Long integer number, 0L or original number
     */
    public static Long null2Zero(Long number){
        return number == null ? 0L : number;
    }


    public static Double setScale(Double number) {
        return new BigDecimal(number)
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue();
    }
    /**
     * Compare two numbers whether they are the same
     * @param number1 Number 1
     * @param number2 Number 2
     * @return Whether they are consistent
     */
    public static boolean equals(Integer number1, Integer number2) {
        if(number1 == null || number2 == null){
            return false;
        }
        return number1.equals(number2);
    }

    /**
     * Divide numbers and keep specified decimal places
     * @param num1 Dividend
     * @param num2 Divisor
     * @param scale Decimal places
     * @return Result
     */
    public static Double divToDouble(Integer num1, Integer num2, int scale){
        if(num2 == null || num2 ==0 || num1 == null || num1 == 0) {
            return 0d;
        }
        return div(num1, num2, scale).doubleValue();
    }

    public static  Double max(List<Double> data){
        if(CollUtils.isEmpty(data)){
            return null;
        }
        return data.stream()
                .max(Comparator.comparingDouble(num -> num))
                .orElse(0d);
    }
    public static  Double min(List<Double> data){
        if(CollUtils.isEmpty(data)){
            return null;
        }
        return data.stream()
                .min(Comparator.comparingDouble(num -> num))
                .orElse(0d);
    }

    public static Double average(List<Double> data){
        if(CollUtils.isEmpty(data)){
            return 0d;
        }
        return data.stream()
                .collect(Collectors.averagingDouble(Double::doubleValue));

    }

    public static Integer toInt(Object obj) {
        return obj == null ? null
                : obj instanceof Integer
                ? (int) obj : null;
    }

    /**
     * Take absolute value, return 0 if null
     * @param number Number
     * @return Absolute value
     */
    public static int abs(Integer number) {
        return number == null
                ? 0
                : Math.abs(number);
    }

    /**
     * Format number to string, pad with 0 if insufficient digits
     *
     * @param originNumber Original number
     * @param digit Number of digits
     * @return String
     */
    public static String  repair0(Integer originNumber, Integer digit){
        StringBuilder number = new StringBuilder(originNumber + "");
        while (number.length() < digit) {
            number.insert(0, "0");
        }
        return number.toString();
    }


    public static String scaleToStr(Integer num, int offset) {
        // 1. Calculate number of digits
        int m = (int) Math.pow(10, offset);
        // 2. Calculate quotient
        int s = num / m;
        // 3. Calculate remainder
        int y = num % m;
        if (y == 0) {
            return Integer.toString(s);
        }
        // 2. Calculate remainder
        return s + "." + y;
    }
}
