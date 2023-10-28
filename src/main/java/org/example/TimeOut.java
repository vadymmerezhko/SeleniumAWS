package org.example;

public class TimeOut {
    int timeoutSeconds;

    static class TimeThread extends Thread {
        private final long startMilliSeconds;
        private final int timeoutSeconds;

        TimeThread(int timeoutSeconds) {
            this.timeoutSeconds = timeoutSeconds;
            startMilliSeconds = System.currentTimeMillis();
        }
        public void run() {
            Waiter.waitSeconds(1);

            if ((System.currentTimeMillis() - startMilliSeconds) / 1000 >= timeoutSeconds) {
                throw new TimeOutException(timeoutSeconds + " seconds timeout exception!");
            }
        }
    }

    public TimeOut(int seconds) {
        timeoutSeconds = seconds;
    }

    public void start() {
        TimeThread timeThread = new TimeThread(timeoutSeconds);
        timeThread.start();
    }
}
