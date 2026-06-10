package com.learnforge.common.utils;

import com.learnforge.common.exceptions.BadRequestException;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Manually execute Violation processing validation result
 * @ClassName ViolationUtils
 * @author wusongsong
 * @since 2022/7/18 20:52
 * @version 1.0.0
 **/
public class ViolationUtils {

    public static <T> void process(Set<ConstraintViolation<T>> violations) {
        if(CollUtils.isEmpty(violations)){
            return;
        }
        String message = violations.stream().map(v -> v.getMessage()).collect(Collectors.joining("|"));
        throw new BadRequestException(message);
    }
}
