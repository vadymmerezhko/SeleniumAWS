package org.example.e2e;

import org.example.data.SmartAssert;
import org.example.data.*;
import org.example.configs.TestConfig;
import org.example.servives.TestServiceInterface;
import org.example.servives.TestServiceManager;
import org.example.tests.BaseTest;
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

        TestServiceInterface testServer = TestServiceManager.getTestServer();
        WebFormPageOutput actual = testServer.fillWebForm(input);
        WebFormPageOutput expected = new WebFormPageOutput();
        expected.getDeliveryDate().setKeyword(actual.getDeliveryDate().getKeyword());

        SmartAssert.assertData(expected, actual);
        Reporter.log("<b>fillWebForm test execution finished.</b>");
    }

    protected void submitWebForm() {
        Reporter.log("<b>submitWebForm test execution started.</b>");

        TestServiceInterface testServer = TestServiceManager.getTestServer();
        TargetPageOutput actual = testServer.submitWebForm();
        TargetPageOutput expected = new TargetPageOutput();

        SmartAssert.assertData(expected, actual);
        Reporter.log("<b>submitWebForm test execution finished.</b>");
    }
}
