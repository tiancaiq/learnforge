package com.learnforge.common.autoconfigure.redisson.aspect;

import com.learnforge.common.autoconfigure.redisson.annotations.Lock;
import com.learnforge.common.exceptions.BizIllegalException;
import com.learnforge.common.utils.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
public class LockAspect {

    private final RedissonClient redissonClient;

    public LockAspect(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    //Acquire lock through around advice, lock before method execution, unlock after method execution based on annotation
    @Around("@annotation(properties)")
    public Object handleLock(ProceedingJoinPoint pjp, Lock properties) throws Throwable {
        if (!properties.autoUnlock() && properties.leaseTime() <= 0) {
            // If not manually release lock, leaseTime must be specified
            throw new BizIllegalException("leaseTime cannot be empty");
        }
        // 1. Parse lock name based on SPEL expression
        String name = getLockName(properties.name(), pjp);
        // 2. Get lock object
        RLock rLock = properties.lockType().getLock(redissonClient, name);
        // 3. Try to acquire lock
        boolean success = properties.lockStrategy().tryLock(rLock, properties);
        if (!success) {
            // Acquire lock failed, end
            return null;
        }
        try {
            // 4. Execute the proxied method
            return pjp.proceed();
        } finally {
            // 5. Release lock
            if (properties.autoUnlock()) {
                rLock.unlock();
            }
        }
    }

    /**
     * SPEL regular expression rules
     */
    private static final Pattern pattern = Pattern.compile("\\#\\{([^\\}]*)\\}");
    /**
     * Method parameter parser
     */
    private static final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * Parse lock name
     * @param name Original lock name
     * @param pjp Join point
     * @return Parsed lock name
     */
    private String getLockName(String name, ProceedingJoinPoint pjp) {
        // 1. Check if there is a SPEL expression
        if (StringUtils.isBlank(name) || !name.contains("#")) {
            // Not exist, return directly
            return name;
        }
        // 2. Build context
        EvaluationContext context = new MethodBasedEvaluationContext(
                TypedValue.NULL, resolveMethod(pjp), pjp.getArgs(), parameterNameDiscoverer);
        // 3. Build parser
        ExpressionParser parser = new SpelExpressionParser();
        // 3. Loop processing
        Matcher matcher = pattern.matcher(name);
        while (matcher.find()) {
            // 2.1. Get expression
            String tmp = matcher.group();
            // 2.2. Try to parse
            Expression expression = parser.parseExpression("#" + matcher.group(1));
            Object value = expression.getValue(context);
            name = name.replace(tmp, ObjectUtils.nullSafeToString(value));
        }
        return name;
    }

    private Method resolveMethod(ProceedingJoinPoint pjp) {
        // 1. Get method signature
        MethodSignature signature = (MethodSignature)pjp.getSignature();
        // 2. Get byte code
        Class<?> clazz = pjp.getTarget().getClass();
        // 3. Method name
        String name = signature.getName();
        // 4. Method parameter list
        Class<?>[] parameterTypes = signature.getMethod().getParameterTypes();
        return tryGetDeclaredMethod(clazz, name, parameterTypes);
    }

    private Method tryGetDeclaredMethod(Class<?> clazz, String name, Class<?> ... parameterTypes){
        try {
            // 5. Reflectively get method
            return clazz.getDeclaredMethod(name, parameterTypes);
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            if (superClass != null) {
                // Try to find from parent class
                return tryGetDeclaredMethod(superClass, name, parameterTypes);
            }
        }
        return null;
    }
}
