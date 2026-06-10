package com.learnforge.common.validate.annotations;

import com.learnforge.common.validate.EnumValidator;
import com.learnforge.common.validate.EnumValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * Enum validation for status
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER, ElementType.FIELD, ElementType.METHOD })
@Constraint(validatedBy = {EnumValidator.class, EnumValueValidator.class})
public @interface EnumValid {
    String message() default "Does not meet business conditions";

    int[] enumeration() default {};

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
