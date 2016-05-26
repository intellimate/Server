package org.intellimate.server.izou;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static javafx.scene.input.KeyCode.M;

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

    public Optional<LockHolder> lock(long time, TimeUnit unit) throws IllegalStateException, InterruptedException {
        reentrantLock.lock();
        try {
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
            } else if (result == 1 ) {
                System.out.println("lock");
                return Optional.of(new LockHolder());
            } else {
                    try {
                        boolean await = waitCondition.await(time, unit);
                        if (await) {
                            System.out.println("lock");
                            return Optional.of(new LockHolder());
                        } else {
                            atomicInteger.decrementAndGet();
                            return Optional.empty();
                        }
                    } catch (InterruptedException e) {
                        atomicInteger.decrementAndGet();
                        throw e;
                    }
            }
        } finally {
            reentrantLock.unlock();
        }
    }

    public class LockHolder {
        private boolean used = false;
        public void unlock() {
            reentrantLock.lock();
            try {
                if (used) {
                    return;
                } else {
                    used = true;
                }
                atomicInteger.decrementAndGet();
                waitCondition.signal();
            } finally {
                reentrantLock.unlock();
            }
        }
    }
}
