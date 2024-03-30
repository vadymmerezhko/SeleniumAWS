package org.example;

import org.example.testng.RetryAnalyzer;
import org.testng.annotations.Test;

public class Selenium12Test extends BaseTest {

    @Test(description = "This method validates the sign up functionality", retryAnalyzer = RetryAnalyzer.class)
    public void signUp1() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp2() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp3() {
        signUp();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 4, retryAnalyzer = RetryAnalyzer.class)
    public void signUp4() {
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
