package com.learnforge.common.autoconfigure.redisson.enums;

import com.learnforge.common.autoconfigure.redisson.annotations.Lock;
import org.redisson.api.RLock;

public enum LockStrategy {
    /**
     * No retry, directly end, return false
     */
    SKIP_FAST() {
        @Override
        public boolean tryLock(RLock lock, Lock properties) throws InterruptedException {
            return lock.tryLock(0, properties.leaseTime(), properties.timeUnit());
        }
    },
    /**
     * No retry, directly end, throw exception
     */
    FAIL_FAST() {
        @Override
        public boolean tryLock(RLock lock, Lock properties) throws InterruptedException {
            boolean success = lock.tryLock(0, properties.leaseTime(), properties.timeUnit());
            if (!success) {
                throw new RuntimeException("Request is too frequent");
            }
            return true;
        }
    },
    /**
     * Retry until timeout, then directly end
     */
    SKIP_AFTER_RETRY_TIMEOUT() {
        @Override
        public boolean tryLock(RLock lock, Lock properties) throws InterruptedException {
            return lock.tryLock(properties.waitTime(), properties.leaseTime(), properties.timeUnit());
        }
    },
    /**
     * Retry until timeout, then throw exception
     */
    FAIL_AFTER_RETRY_TIMEOUT() {
        @Override
        public boolean tryLock(RLock lock, Lock properties) throws InterruptedException {
            boolean success = lock.tryLock(properties.waitTime(), properties.leaseTime(), properties.timeUnit());
            if (!success) {
                throw new RuntimeException("Request timeout");
            }
            return true;
        }
    },
    /**
     * Keep retrying until success
     */
    KEEP_RETRY() {
        @Override
        public boolean tryLock(RLock lock, Lock properties) throws InterruptedException {
            lock.lock(properties.leaseTime(), properties.timeUnit());
            return true;
        }
    },
    ;

    public abstract boolean tryLock(RLock lock, Lock properties) throws InterruptedException;
}
