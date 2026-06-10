package com.learnforge.common.utils;

import cn.hutool.core.date.LocalDateTimeUtil;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Time utility class for local time operations, includes all methods from LocalDateTimeUtil and custom LocalDateTime operations and constants
 *
 * @author wusongsong
 * @version 1.0.0 1.0
 * @see 1.0
 * @since From which version this class's functionality is supported
 */
public class DateUtils extends LocalDateTimeUtil {

    public static final String DEFAULT_YEAR_FORMAT = "yyyy";
    public static final String DEFAULT_MONTH_FORMAT = "yyyy-MM";
    public static final String DEFAULT_MONTH_FORMAT_SLASH = "yyyy/MM";
    public static final String DEFAULT_MONTH_FORMAT_EN = "yyyy年MM月";
    public static final String DEFAULT_MONTH_FORMAT_COMPACT = "yyyyMM";
    public static final String DEFAULT_WEEK_FORMAT = "yyyy-ww";
    public static final String DEFAULT_WEEK_FORMAT_EN = "yyyy年ww周";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT_EN = "yyyy年MM月dd日";
    public static final String DEFAULT_DATE_FORMAT_COMPACT = "yyyyMMdd";
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DEFAULT_DATE_TIME_COMPACT = "yyyyMMddHHmmss";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DAY = "DAY";
    public static final String MONTH = "MONTH";
    public static final String WEEK = "WEEK";
    public static final long MAX_MONTH_DAY = 30L;
    public static final long MAX_3_MONTH_DAY = 90L;
    public static final long MAX_YEAR_DAY = 365L;

    public static final DateTimeFormatter SIGN_DATE_SUFFIX_FORMATTER =
            DateTimeFormatter.ofPattern(":yyyyMM");

    public static final DateTimeFormatter POINTS_BOARD_SUFFIX_FORMATTER =
            DateTimeFormatter.ofPattern("yyyyMM");

    public static final String TIME_ZONE_8 = "GMT+8";

    /**
     * Get UTC time
     *
     * @param localDateTime Conversion time
     * @return UTC time
     */
    public static LocalDateTime getUTCTime(LocalDateTime localDateTime) {
        ZoneId australia = ZoneId.of("Asia/Shanghai");
        ZonedDateTime dateAndTimeInSydney = ZonedDateTime.of(localDateTime, australia);
        ZonedDateTime utcDate = dateAndTimeInSydney.withZoneSameInstant(ZoneOffset.UTC);
        return utcDate.toLocalDateTime();
    }

    /**
     * Get Asia time
     *
     * @param localDateTime Conversion time
     * @return Asia time
     */
    public static LocalDateTime getAsiaTime(LocalDateTime localDateTime) {
        ZoneId australia = ZoneId.of("Asia/Shanghai");
        ZonedDateTime dateAndTimeInSydney = ZonedDateTime.of(localDateTime, ZoneOffset.UTC);
        ZonedDateTime utcDate = dateAndTimeInSydney.withZoneSameInstant(australia);
        return utcDate.toLocalDateTime();
    }

    /**
     * Get start of a day: 0:00
     *
     * @param localDateTime Specified date
     * @return Converted time
     */
    public static LocalDateTime getDayStartTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toLocalDate().atStartOfDay();
    }

    /**
     * Get end of a day: 23:59:59
     *
     * @param localDateTime Specified date
     * @return Converted time
     */
    public static LocalDateTime getDayEndTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
    }

    public static Date addDays(int i) {
        Calendar c = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE_8));
        c.add(Calendar.DAY_OF_MONTH, i);
        return c.getTime();
    }



    public static LocalDate getMonthBegin(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonth(), 1);
    }

    public static LocalDate getMonthEnd(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonthValue() + 1, 1).minusDays(1);
    }

    public static LocalDateTime getMonthBeginTime(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonth(), 1).atStartOfDay();
    }

    public static LocalDateTime getMonthEndTime(LocalDate date) {
        return LocalDate.of(date.getYear(), date.getMonthValue() + 1, 1)
                .minusDays(1).atTime(LocalTime.MAX);
    }

    public static LocalDateTime getWeekBeginTime(LocalDate now) {
        return now.minusDays(now.getDayOfWeek().getValue() - 1).atStartOfDay();
    }

    public static LocalDateTime getWeekEndTime(LocalDate now) {
        return LocalDateTime.of(now.plusDays(8 - now.getDayOfWeek().getValue()), LocalTime.MAX);
    }

    /**
     * Get recent 15 days date (excluding today), format MM.dd
     *
     * @return
     */
    public static List<String> last15Day(){
        // 1. Define date list
        List<String> days = new ArrayList<>();
        // 2. Get time 15 days ago
        LocalDateTime time = now().minusDays(15);
        // 3. For loop traversal
        for (int count = 0; count < 15; count++){
            // 3.1. Format time
            days.add(String.format("%s.%s",
                    NumberUtils.repair0(time.getMonthValue(),2), NumberUtils.repair0(time.getDayOfMonth(), 2)));
            // 3.2. Add 1 day to date
            time = time.plusDays(1);
        }
        // 4. Return Result
        return days;
    }
}
