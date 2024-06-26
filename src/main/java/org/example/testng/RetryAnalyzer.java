package org.example.testng;

import org.example.constants.Settings;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Retry analyser class.
 * Implements logic to retry TestNG tests in case of failure.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;

    /**
     * @see org.testng.IRetryAnalyzer#retry(org.testng.ITestResult)
     * This method decides how many times a test needs to be rerun.
     * TestNg will call this method every time a test fails. So we
     * can put some code in here to decide when to rerun the test.
     * Note: This method will return true if a tests needs to be retried
     * and false it not.
     */
    @Override
    public boolean retry(ITestResult result) {

        if (retryCount < Settings.TEST_RETRY_LIMIT)
        {
            retryCount++;
            return true;
        }
        return false;
    }
}