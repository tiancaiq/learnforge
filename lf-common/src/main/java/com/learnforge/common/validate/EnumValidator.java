package com.learnforge.common.validate;

import com.learnforge.common.enums.BaseEnum;
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
public class EnumValidator implements ConstraintValidator<EnumValid, BaseEnum> {

    private int[] enums = null;

    @Override
    public void initialize(EnumValid enumValid) {
        this.enums = enumValid.enumeration();
        log.info("payload>>{}",ArrayUtils.toString(enumValid.payload()));
    }

    @Override
    public boolean isValid(BaseEnum em, ConstraintValidatorContext context) {
        // No null validation
        if(em == null){
            return true;
        }
        //No enum value configured, no validation
        if (ArrayUtils.isEmpty(enums)) {
            return true;
        }
        for (int e : enums) {
            if (e == em.getValue()) {
                return true;
            }
        }
        return false;
    }
}
