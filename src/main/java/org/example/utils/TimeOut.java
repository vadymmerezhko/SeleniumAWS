package org.example.utils;

import lombok.extern.slf4j.Slf4j;

/**
 * Timeout utils class.
 */
@Slf4j
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
         * Calls system exit when timeout period expires.
         */
        public void run() {
            while (true) {
                if ((System.currentTimeMillis() - startMilliSeconds) / 1000 >= timeoutSeconds) {
                    log.error("System exit after timeout {} seconds.", timeoutSeconds);
                    System.exit(1);
                }
                Waiter.waitSeconds(1);
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
