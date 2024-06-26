package org.example;
import org.example.testng.RetryAnalyzer;
import org.testng.annotations.*;

public class Selenium1Test extends BaseTest {

    @Test(description = "This method validates the sign up functionality", invocationCount = 1, retryAnalyzer = RetryAnalyzer.class)
    public void signUp1() {
        failSignUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp2() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp3() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 1, retryAnalyzer = RetryAnalyzer.class)
    public void signUp4() {
        failSignUp();
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
