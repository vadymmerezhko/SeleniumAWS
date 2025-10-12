package com.smarte2e.e2e;

import com.smarte2e.data.SmartAssert;
import com.smarte2e.configs.TestConfig;
import com.smarte2e.data.TargetPageOutput;
import com.smarte2e.data.WebFormPageInput;
import com.smarte2e.data.WebFormPageOutput;
import com.smarte2e.servives.TestServiceInterface;
import com.smarte2e.servives.TestServiceManager;
import com.smarte2e.tests.BaseTest;
import org.testng.Assert;
import org.testng.Reporter;

public class CommonTest extends BaseTest {
    static private final TestConfig config = TestConfig.getInstance();

    protected void fillWebForm() {
        WebFormPageInput webFormPageInput = new WebFormPageInput();
        fillWebForm(webFormPageInput);
    }

    protected void failFillWebForm() {
        fillWebForm();
        if (config.getDebugFail()) {
            Assert.fail("Test is failed for debug purpose.");
        }
    }

    private void fillWebForm(WebFormPageInput input) {
        Reporter.log("<b>fillWebForm test execution started.</b>");

        TestServiceInterface testService = TestServiceManager.getService();
        WebFormPageOutput actual = testService.fillWebForm(input);
        WebFormPageOutput expected = new WebFormPageOutput();
        expected.getDeliveryDate().setKeyword(input.getDeliveryDate().getKeyword());

        SmartAssert.assertData(expected, actual);
        Reporter.log("<b>fillWebForm test execution finished.</b>");
    }

    protected void submitWebForm() {
        Reporter.log("<b>submitWebForm test execution started.</b>");

        TestServiceInterface testService = TestServiceManager.getService();
        TargetPageOutput actual = testService.submitWebForm();
        TargetPageOutput expected = new TargetPageOutput();

        SmartAssert.assertData(expected, actual);
        Reporter.log("<b>submitWebForm test execution finished.</b>");
    }
}
