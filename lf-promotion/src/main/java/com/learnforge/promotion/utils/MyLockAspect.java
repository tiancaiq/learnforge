package com.learnforge.promotion.utils;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class MyLockAspect implements Ordered {

    private final MyLockFactory lockFactory;

    @Around("@annotation(myLock)")
    public Object tryLock(ProceedingJoinPoint pjp, MyLock myLock) throws Throwable {
        //1. create lock obj
       RLock lock = lockFactory.getLock(myLock.lockType(),myLock.name());

        //2. try lock
        boolean isLock = myLock.lockStrategy().tryLock(lock,myLock);

        if(!isLock){
            return null;
        }
//3. if success
        try {
            return pjp.proceed();
        } finally {

            //release
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
