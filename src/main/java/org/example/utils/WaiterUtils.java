package org.example.utils;


import lombok.extern.slf4j.Slf4j;

/**
 * Waiter class.
 */
@Slf4j
public final class WaiterUtils {

    private WaiterUtils() {}

    /**
     * Waits for n seconds (from 0 to 600).
     * @param seconds The number of seconds to wait.
     */
    public static void waitSeconds(int seconds) {
        DataValidationUtils.validateRange(seconds,0, 10 * 60, "milliSeconds");
        try {
            log.debug("Wait for {} seconds began.", seconds);
            Thread.sleep(seconds * 1000L);
            log.debug("Wait for {} seconds finished.", seconds);
        } catch (Throwable e) {
            throw new RuntimeException(String.format(
                    "Wait for %d seconds failed.", seconds), e);
        }
    }

    /**
     * Waits for n milliseconds (from 0 to 60,000).
     * @param milliSeconds The number of milliseconds to wait.
     */
    public static void waitMilliSeconds(long milliSeconds) {
        DataValidationUtils.validateRange(milliSeconds,0, 60 * 1000, "milliSeconds");
        try {
            Thread.sleep(milliSeconds);
            log.debug("Wait to {} milliseconds.", milliSeconds);
        } catch (Throwable e) {
            throw new RuntimeException(String.format(
                    "Wait for %d milliseconds failed.", milliSeconds), e);
        }
    }
}
