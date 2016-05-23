package org.intellimate.server.izou;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A lock with bounded waiting
 * @author LeanderK
 * @version 1.0
 */
//TODO build clever hack for multithreading
public class BoundedLock {
    private AtomicInteger atomicInteger = new AtomicInteger(0);
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Condition waitCondition = reentrantLock.newCondition();
    private final int max;

    public BoundedLock(int max) {
        this.max = max;
    }

    public boolean lock(long time, TimeUnit unit) throws IllegalStateException, InterruptedException {
        int result = atomicInteger.updateAndGet(value -> {
            if (value >= max) {
                return -1;
            } else {
                value++;
                return value;
            }
        });
        if (result == -1) {
            throw new IllegalStateException("Bound exceeded");
        } else if (result == 1 ){
            return true;
        } else {
            reentrantLock.lock();
            try {
                return waitCondition.await(time, unit);
            } finally {
                reentrantLock.unlock();
            }
        }
    }

    public void unlock() {
        atomicInteger.decrementAndGet();
        reentrantLock.lock();
        try {
            waitCondition.signal();
        } finally {
            reentrantLock.unlock();
        }
    }
}
