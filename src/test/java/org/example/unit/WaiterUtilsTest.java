package org.example.unit;

import org.example.exceptions.SmartValidationException;
import org.example.utils.WaiterUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WaiterUtilsTest {

    @Test
    public void testWaitSecondsPositive() {
        long startTime = System.currentTimeMillis();
        int waitTime = 1; // 1 second
        WaiterUtils.waitSeconds(waitTime);
        long endTime = System.currentTimeMillis();
        Assert.assertTrue((endTime - startTime) >= waitTime * 1000, "Did not wait for at least " + waitTime + " seconds.");
    }

    @Test
    public void testWaitMilliSecondsPositive() {
        long startTime = System.currentTimeMillis();
        long waitTime = 500; // 500 milliseconds
        WaiterUtils.waitMilliSeconds(waitTime);
        long endTime = System.currentTimeMillis();
        Assert.assertTrue((endTime - startTime) >= waitTime, "Did not wait for at least " + waitTime + " milliseconds.");
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testWaitSecondsNegative() {
        WaiterUtils.waitSeconds(-1);
    }

    @Test(expectedExceptions = SmartValidationException.class)
    public void testWaitMilliSecondsNegative() {
     WaiterUtils.waitMilliSeconds(-1);
    }

    @Test
    public void testWaitSecondsInterrupted() throws Exception {
        Thread testThread = new Thread(() -> {
            try {
                WaiterUtils.waitSeconds(2);
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
                WaiterUtils.waitMilliSeconds(2000);
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