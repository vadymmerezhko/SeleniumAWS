package org.example.e2e;
import org.example.annotations.RunAlone;
import org.example.testng.RetryAnalyzer;
import org.testng.annotations.*;

public class Selenium1Test extends CommonTest {

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
