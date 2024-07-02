package org.example.utils;


/**
 * Waiter class.
 */
public final class WaiterUtils {

    private WaiterUtils() {}

    /**
     * Waits for n seconds.
     * @param seconds The number of seconds to wait.
     */
    public static void waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Waits for n milliseconds.
     * @param milliSeconds The number of milliseconds to wait.
     */
    public static void waitMilliSeconds(long milliSeconds) {
        try {
            Thread.sleep(milliSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
