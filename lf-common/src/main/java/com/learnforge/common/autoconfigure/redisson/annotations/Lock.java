package com.learnforge.common.autoconfigure.redisson.annotations;

import com.learnforge.common.autoconfigure.redisson.enums.LockStrategy;
import com.learnforge.common.autoconfigure.redisson.enums.LockType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Distributed lock
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Lock {

    /**
     * Expression for lock key, supports SPEL expression
     */
    String name();

    /**
     * Blocking timeout duration, if waitTime is not specified, use Redisson's default duration
     */
    long waitTime() default 1;

    /**
     * Lock auto release duration, default is -1, which is actually 30 seconds + watchdog mode
     */
    long leaseTime() default -1;

    /**
     * Time unit, default is seconds
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * If set to false, the lock is not released when the method ends, but is automatically released after leaseTime
     */
    boolean autoUnlock() default true;

    /**
     * Lock type, including: reentrant lock, fair lock, read lock, write lock
     */
    LockType lockType() default LockType.DEFAULT;

    /**
     * Lock strategy, including 5 types, default strategy is keep trying to acquire the lock until success or timeout, timeout then throw exception
     */
    LockStrategy lockStrategy() default LockStrategy.FAIL_AFTER_RETRY_TIMEOUT;
}
