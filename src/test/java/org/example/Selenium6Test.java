package org.example;
import org.example.driver.WebDriverFactory;
import org.testng.annotations.*;

public class Selenium6Test extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void openBrowser()
    {
        //driver = WebDriverFactory.getDriver();
    }
    @AfterMethod(alwaysRun = true)
    public void decrementThreadPool() {
        //LoadBalancer.getInstance().decrementServerThreadCount();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = org.example.RetryAnalyzer.class)
    public void signUp1()
    {
        signUp();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = org.example.RetryAnalyzer.class)
    public void signUp2()
    {
        signUp();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = org.example.RetryAnalyzer.class)
    public void signUp3()
    {
        signUp();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = org.example.RetryAnalyzer.class)
    public void signUp4()
    {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = org.example.RetryAnalyzer.class)
    public void signUp5() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = org.example.RetryAnalyzer.class)
    public void signUp6() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = org.example.RetryAnalyzer.class)
    public void signUp7() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = org.example.RetryAnalyzer.class)
    public void signUp8() {
        signUp();
    }
}
