package com.smarte2e.e2e;

import com.smarte2e.data.LoginPageInput;
import com.smarte2e.data.LoginPageOutput;
import com.smarte2e.data.ProductsPageHeaderOutput;
import com.smarte2e.data.SmartAssert;
import com.smarte2e.servives.TestServiceInterface;
import com.smarte2e.servives.TestServiceManager;
import com.smarte2e.testng.RetryAnalyzer;
import org.testng.Reporter;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class UserLoginTest extends CommonTest {
    static final String STANDARD_USER = "StandardUser";
    static final String VISUAL_USER = "VisualUser";

    @DataProvider(name = "userLoginData")
    public Object[][] loginPageData() {
        return new Object[][] {
                {STANDARD_USER},
                {VISUAL_USER}
        };
    }

    @Test(description = "This method validates the Login form functionality",
            retryAnalyzer = RetryAnalyzer.class,
            dataProvider = "userLoginData")
    public void testUserLogin(String dataSetName) {
        LoginPageInput loginInput = new LoginPageInput();
        LoginPageOutput loginExpectedOutput = new LoginPageOutput();
        loginInput.setDataSetName(dataSetName);
        loginExpectedOutput.setDataSetName(dataSetName);

        TestServiceInterface testService = TestServiceManager.getService();
        LoginPageOutput loginActualOutput = testService.fillLoginPage(loginInput);
        SmartAssert.assertData(loginExpectedOutput, loginActualOutput);

        ProductsPageHeaderOutput productsHeaderActual = testService.submitLoginPage();
        ProductsPageHeaderOutput productsHeaderExpected = new ProductsPageHeaderOutput();

        SmartAssert.assertData(productsHeaderExpected, productsHeaderActual);
        Reporter.log("<b>Standard User Login test execution finished.</b>");
    }
}
