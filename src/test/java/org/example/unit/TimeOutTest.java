package org.example.unit;

import org.example.helpers.TimeOut;
import org.example.exceptions.SmartTimeOutException;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TimeOutTest {
    @Test
    public void testTimeoutNotExpiredInitially() {
        TimeOut timeOut = new TimeOut("TestTimeout", 2);
        Assert.assertFalse(timeOut.getExpired(),
                "Timeout should not be expired initially.");
    }

    @Test
    public void testTimeoutExpires() throws InterruptedException {
        TimeOut timeOut = new TimeOut("TestTimeout", 1);
        timeOut.start();
        Thread.sleep(1500); // Wait more than 1 second to ensure
        // timeout has a chance to expire
        Assert.assertTrue(timeOut.getExpired(),
                "Timeout should expire after 1 second.");
    }

    @Test
    public void testCheckExpiredThrowsException() {
        TimeOut timeOut = new TimeOut("TestTimeout", 1);
        timeOut.start();
        try {
            Thread.sleep(1500); // Ensure the timeout expires
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Assert.assertThrows(SmartTimeOutException.class, timeOut::checkExpired);
    }

    @Test
    public void testCheckExpiredNoExceptionIfNotExpired() {
        TimeOut timeOut = new TimeOut("TestTimeout", 5);
        timeOut.start();
        // Do not wait for timeout to expire; check immediately
        try {
            timeOut.checkExpired();
        } catch (SmartTimeOutException e) {
            throw new AssertionError("TimeOutException should not " +
                    "be thrown if timeout is not expired.");
        }
    }
}