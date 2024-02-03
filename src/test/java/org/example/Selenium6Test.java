package org.example;
import org.example.balancer.LoadBalancer;
import org.example.driver.WebDriverFactory;
import org.example.testng.RetryAnalyzer;
import org.testng.annotations.*;

public class Selenium6Test extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void openBrowser()
    {
        driver = WebDriverFactory.getDriver();
    }
    @AfterMethod(alwaysRun = true)
    public void decrementThreadPool() {
        //driver.manage().deleteAllCookies();
        LoadBalancer.getInstance().decrementServerThreadCount();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp1()
    {
        signUp();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp2()
    {
        signUp();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp3()
    {
        signUp();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp4()
    {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp5() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp6() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp7() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp8() {
        signUp();
    }
}
