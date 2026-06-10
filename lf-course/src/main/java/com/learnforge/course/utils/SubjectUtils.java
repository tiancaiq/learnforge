package com.learnforge.course.utils;

import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.utils.ObjectUtils;
import com.learnforge.common.utils.ReflectUtils;
import com.learnforge.common.utils.StringUtils;
import com.learnforge.course.domain.po.Subject;

import java.util.ArrayList;
import java.util.List;

/**
 * Question Utility Class
 *
 * @ClassName SubjectUtils
 * @Author wusongsong
 * @Date 2022/7/15 17:13
 * @Version
 **/
public class SubjectUtils {

    /**
     * Set options in the question
     *
     * @param subject Question
     * @param options Options
     */
    public static void setOptions(Subject subject, List<String> options) {
        if (CollUtils.isNotEmpty(options)) {
            for (int count = 0; count < options.size(); count++) {
                ReflectUtils.setFieldValue(subject, "option" + (count + 1), options.get(count));
            }
        }
    }

    /**
     * Get options from the question
     *
     * @param subject Question
     * @return Options
     */
    public static List<String> getOptions(Subject subject) {
        List<String> options = new ArrayList<>();
        for (int count = 1; count <= 10; count++) {
            Object option = ReflectUtils.getFieldValue(subject, "option" + count);
            if (ObjectUtils.isEmpty(option) || StringUtils.isEmpty((String)option)) {
                return options;
            }
            options.add((String) option);
        }
        return options;
    }
}
