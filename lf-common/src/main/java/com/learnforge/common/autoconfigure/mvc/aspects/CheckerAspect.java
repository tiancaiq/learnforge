package com.learnforge.common.autoconfigure.mvc.aspects;

import com.learnforge.common.utils.ArrayUtils;
import com.learnforge.common.utils.CollUtils;
import com.learnforge.common.validate.Checker;
import com.learnforge.common.validate.annotations.ParamChecker;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

import java.util.List;

@Aspect
@Slf4j
@SuppressWarnings("all")
public class CheckerAspect {

    @Before("@annotation(paramChecker)")
    public void before(JoinPoint joinPoint, ParamChecker paramChecker) {
        Object[] args = joinPoint.getArgs();
        if(ArrayUtils.isNotEmpty(args)){
            //Traverse method parameters, check if parameter implements Checker interface
            for (Object arg : args){
                if(arg instanceof Checker) {
                    //Call check method, validate business logic
                    ((Checker)arg).check();
                }else if(arg instanceof List){
                    //If parameter is a collection, also validate
                    CollUtils.check((List) arg);
                }
            }
        }
    }
}
