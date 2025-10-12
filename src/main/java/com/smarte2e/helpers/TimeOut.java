package com.smarte2e.helpers;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.exceptions.SmartTimeOutException;
import com.smarte2e.utils.TimerUtils;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Timeout utils class.
 */
@Slf4j
public class TimeOut {
    private final int timeoutSeconds;
    private final String name;
    private final AtomicBoolean isExpired = new AtomicBoolean(false);

    /**
     * Timeout constructor.
     * @param  name The timeout name.
     * @param timeoutSeconds The timeout seconds.
     */
    public TimeOut(String name, int timeoutSeconds) {
        this.name = name;
        this.timeoutSeconds = timeoutSeconds;
    }

    /**
     * Runs timeout timer thread till the timeout expires.
     */
    public void start() {
        long startMilliSeconds = System.currentTimeMillis();
        Thread thread = new Thread(() -> {
            while ((System.currentTimeMillis() - startMilliSeconds) / 1000 < timeoutSeconds) {
                TimerUtils.waitSeconds(1);
            }
            isExpired.set(true);
        });
        thread.start();
    }

    /**
     * Returns true if the timeout is expired or false otherwise;
     * @return The expired flag.
     */
    public boolean getExpired() {
        return isExpired.get();
    }

    /**
     * Throws timeout exception if timeout expires.
     */
    public void checkExpired() {
        if (getExpired()) {
            throw new SmartTimeOutException(String.format("%s timeout expired.", name));
        }
    }
}
