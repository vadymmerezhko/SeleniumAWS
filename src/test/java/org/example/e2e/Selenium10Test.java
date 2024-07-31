package org.example.e2e;

import org.example.testng.RetryAnalyzer;
import org.testng.annotations.Test;

public class Selenium10Test extends CommonTest {

    @Test(description = "This method validates the Web Form functionality", retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm1() {
        fillWebForm();
        submitWebForm();
    }

    @Test(description = "This method validates the Web Form functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm2() {
        fillWebForm();
        submitWebForm();
    }

    @Test(description = "This method validates the Web Form functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm3() {
        fillWebForm();
        submitWebForm();
    }

    @Test(description = "This method validates the Web Form functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm4() {
        fillWebForm();
        submitWebForm();
    }

    @Test(description = "This method validates the Web Form functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm5() {
        fillWebForm();
        submitWebForm();
    }

    @Test(description = "This method validates the Web Form functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm6() {
        fillWebForm();
        submitWebForm();
    }

    @Test(description = "This method validates the Web Form functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm7() {
        fillWebForm();
    }

    @Test(description = "This method validates the Web Form functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm8() {
        fillWebForm();
        submitWebForm();
    }
}
