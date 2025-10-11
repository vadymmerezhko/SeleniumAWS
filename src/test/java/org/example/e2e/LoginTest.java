package org.example.e2e;

import org.example.annotations.RunAlone;
import org.example.data.LoginPageInput;
import org.example.data.LoginPageOutput;
import org.example.data.ProductsPageHeaderOutput;
import org.example.data.SmartAssert;
import org.example.servives.TestServiceInterface;
import org.example.servives.TestServiceManager;
import org.example.testng.RetryAnalyzer;
import org.testng.Reporter;
import org.testng.annotations.Test;

public class LoginTest extends CommonTest {

    @RunAlone
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
