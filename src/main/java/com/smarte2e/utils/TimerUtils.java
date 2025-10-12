package com.smarte2e.utils;


import lombok.extern.slf4j.Slf4j;
import com.smarte2e.exceptions.SmartRuntimeException;

/**
 * Timer utils class.
 */
@Slf4j
public final class TimerUtils {

    private TimerUtils() {}

    /**
     * Waits for n seconds (from 0 to 600).
     * @param seconds The number of seconds to wait.
     */
    public static void waitSeconds(int seconds) {
        DataValidator.range(seconds,0, 10 * 60, "milliSeconds");
        try {
            log.debug("Wait for {} seconds began.", seconds);
            Thread.sleep(seconds * 1000L);
            log.debug("Wait for {} seconds finished.", seconds);
        }
        catch (Throwable e) {
            throw new SmartRuntimeException(String.format(
                    "Wait for %d seconds failed.", seconds), e);
        }
    }

    /**
     * Waits for n milliseconds (from 0 to 60,000).
     * @param milliSeconds The number of milliseconds to wait.
     */
    public static void waitMilliSeconds(long milliSeconds) {
        DataValidator.range(milliSeconds,0, 60 * 1000, "milliSeconds");
        try {
            Thread.sleep(milliSeconds);
            log.debug("Wait to {} milliseconds.", milliSeconds);
        } catch (Throwable e) {
            throw new SmartRuntimeException(String.format(
                    "Wait for %d milliseconds failed.", milliSeconds), e);
        }
    }
}
