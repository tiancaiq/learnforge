package com.learnforge.common.validate;

import com.learnforge.common.utils.ArrayUtils;
import com.learnforge.common.validate.annotations.EnumValid;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Enum validator validation logic
 *
 **/
@Slf4j
public class EnumValueValidator implements ConstraintValidator<EnumValid, Integer> {

    private int[] enums = null;

    @Override
    public void initialize(EnumValid enumValid) {
        this.enums = enumValid.enumeration();
        log.info("payload>>{}",ArrayUtils.toString(enumValid.payload()));
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // No null validation
        if(value == null){
            return true;
        }
        //No enum value configured, no validation
        if (ArrayUtils.isEmpty(enums)) {
            return true;
        }
        for (int e : enums) {
            if (e == value) {
                return true;
            }
        }
        return false;
    }
}
