package org.example;

import java.util.concurrent.TimeUnit;

public class Waiter {
    public static void waitSeconds(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void waitMilliSeconds(int milliSeconds) {
        try {
            TimeUnit.MICROSECONDS.sleep(milliSeconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
