package org.example;
import org.example.testng.RetryAnalyzer;
import org.testng.annotations.*;

public class Selenium2Test extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void openBrowser()
    {
        //driver = WebDriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void decrementThreadPool() {
        driver.manage().deleteAllCookies();
        //LoadBalancer.getInstance().decrementServerThreadCount();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = RetryAnalyzer.class)
    public void signUp1()
    {
        signUp(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = RetryAnalyzer.class)
    public void signUp2()
    {
        signUp(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = RetryAnalyzer.class)
    public void signUp3()
    {
        signUp(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = RetryAnalyzer.class)
    public void signUp4()
    {
        signUp(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = RetryAnalyzer.class)
    public void signUp5() {
        signUp(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = RetryAnalyzer.class)
    public void signUp6() {
        signUp(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = RetryAnalyzer.class)
    public void signUp7() {
        signUp(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16, retryAnalyzer = RetryAnalyzer.class)
    public void signUp8() {
        signUp(this.getClass().getName(), new Object(){}.getClass().getEnclosingMethod().getName());
    }
}
