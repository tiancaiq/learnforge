package com.learnforge.common.utils;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;

import java.lang.reflect.Method;

public class AspectUtils {

    /**
     * Get intercepted method object
     * MethodSignature.getMethod() gets the method object of the top-level interface or parent class
     * So should use reflection to get the method object of the current object
     */
    public static Method getMethod(ProceedingJoinPoint pjp) {
        //Get parameter type
        Signature sig = pjp.getSignature();
        if (sig instanceof MethodSignature) {
            MethodSignature methodSignature = (MethodSignature) sig;
            return methodSignature.getMethod();
        } else {
            throw new IllegalArgumentException("It's not method");
        }
    }

    /**
     * In aop aspect, SPEL expression formats the formatter
     * Convert to specified value
     *
     * @param formatter
     * @param method
     * @param args
     * @return
     */
    public static String parse(String formatter, Method method, Object[] args) {
        LocalVariableTableParameterNameDiscoverer nameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        return SPELUtils.parse(formatter, nameDiscoverer.getParameterNames(method), args);
    }
}
