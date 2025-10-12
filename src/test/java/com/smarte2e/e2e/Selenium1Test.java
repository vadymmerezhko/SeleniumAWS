package com.smarte2e.e2e;
import com.smarte2e.annotations.RunAlone;
import com.smarte2e.testng.RetryAnalyzer;
import org.testng.annotations.*;

public class Selenium1Test extends com.smarte2e.e2e.CommonTest {

    @RunAlone
    @Test(description = "This method validates the Web Form functionality", retryAnalyzer = RetryAnalyzer.class)
    public void testWebForm1() {
        failFillWebForm();
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
    public void fillWebForm7() {
        fillWebForm();
        submitWebForm();
    }

    @Test(description = "This method validates the Web Form functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void fillWebForm8() {
        fillWebForm();
        submitWebForm();
    }
}
