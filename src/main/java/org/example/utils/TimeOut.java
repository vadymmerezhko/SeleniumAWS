package org.example.utils;

/**
 * Timeout utils class.
 */
public class TimeOut {
    int timeoutSeconds;

    static class TimeThread extends Thread {
        private final long startMilliSeconds;
        private final int timeoutSeconds;

        TimeThread(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            startMilliSeconds = System.currentTimeMillis();
        }

        /**
         * Runs timeout timer thread.
         * Throws timeout exception when timeout period expires.
         */
        public void run() {
            Waiter.waitSeconds(1);

            if ((System.currentTimeMillis() - startMilliSeconds) / 1000 >= timeoutSeconds) {
                throw new TimeOutException(timeoutSeconds + " seconds timeout exception!");
            }
        }
    }

    /**
     * Timeout constructor.
     * @param seconds The timeout seconds.
     */
    public TimeOut(int seconds) {
        timeoutSeconds = seconds;
    }

    /**
     * Starts the timeout timer.
     */
    public void start() {
        TimeThread timeThread = new TimeThread(timeoutSeconds);
        timeThread.start();
    }
}
