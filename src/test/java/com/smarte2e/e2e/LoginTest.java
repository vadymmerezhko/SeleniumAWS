package com.smarte2e.e2e;

import com.smarte2e.data.LoginPageInput;
import com.smarte2e.data.LoginPageOutput;
import com.smarte2e.data.ProductsPageHeaderOutput;
import com.smarte2e.data.SmartAssert;
import com.smarte2e.servives.TestServiceInterface;
import com.smarte2e.servives.TestServiceManager;
import com.smarte2e.testng.RetryAnalyzer;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class LoginTest extends CommonTest {

    @Test(description = "This method validates the Login form functionality", retryAnalyzer = RetryAnalyzer.class)
    public void testStandardUserLogin() {
        LoginPageInput loginPageInput = new LoginPageInput();
        TestServiceInterface testService = TestServiceManager.getService();
        LoginPageOutput loginActual = testService.fillLoginPage(loginPageInput);
        LoginPageOutput loginExpected = new LoginPageOutput();

        SmartAssert.assertData(loginExpected, loginActual);

        ProductsPageHeaderOutput productsHeaderActual = testService.submitLoginPage();
        ProductsPageHeaderOutput productsHeaderExpected = new ProductsPageHeaderOutput();

        SmartAssert.assertData(productsHeaderExpected, productsHeaderActual);
        Reporter.log("<b>Standard User Login test execution finished.</b>");
    }
}
