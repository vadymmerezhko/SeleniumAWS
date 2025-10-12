package com.smarte2e.unit;

import com.smarte2e.exceptions.SmartValidationException;
import com.smarte2e.utils.TimerUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TimerUtilsTest {

    @Test
    public void testWaitSecondsPositive() {
        long startTime = System.currentTimeMillis();
        int waitTime = 1; // 1 second
        TimerUtils.waitSeconds(waitTime);
        long endTime = System.currentTimeMillis();
        Assert.assertTrue((endTime - startTime) >= waitTime * 1000, "Did not wait for at least " + waitTime + " seconds.");
    }

    @Test
    public void testWaitMilliSecondsPositive() {
        long startTime = System.currentTimeMillis();
        long waitTime = 500; // 500 milliseconds
        TimerUtils.waitMilliSeconds(waitTime);
        long endTime = System.currentTimeMillis();
        Assert.assertTrue((endTime - startTime) >= waitTime, "Did not wait for at least " + waitTime + " milliseconds.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testWaitSecondsNegative() {
        TimerUtils.waitSeconds(-1);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testWaitMilliSecondsNegative() {
     TimerUtils.waitMilliSeconds(-1);
    }

    @Test
    public void testWaitSecondsInterrupted() throws Exception {
        Thread testThread = new Thread(() -> {
            try {
                TimerUtils.waitSeconds(2);
                Assert.fail("Exception was not thrown after " +
                        "wait thread has been interrupted.");
            } catch (RuntimeException rte) {
                // Ignore exception
            } catch (Exception e) {
                Assert.fail(String.format("Wrong exception: %s",
                        e.getClass().getSimpleName()));
            }
        });
        testThread.start();
        Thread.sleep(500); // Wait half a second before interrupting
        testThread.interrupt();
        testThread.join(); // Ensure test completes
    }

    @Test
    public void testWaitMilliSecondsInterrupted() throws Exception {
        Thread testThread = new Thread(() -> {
            try {
                TimerUtils.waitMilliSeconds(2000);
                Assert.fail("Exception was not thrown after " +
                        "wait thread has been interrupted.");
            } catch (RuntimeException rte) {
                // Ignore exception
            } catch (Exception e) {
                Assert.fail(String.format("Wrong exception: %s",
                        e.getClass().getSimpleName()));
            }
        });
        testThread.start();
        Thread.sleep(500); // Wait half a second before interrupting
        testThread.interrupt();
        testThread.join(); // Ensure test completes
    }
}