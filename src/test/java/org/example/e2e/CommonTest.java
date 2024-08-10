package org.example.e2e;

import org.example.asserts.SmartAssert;
import org.example.data.*;
import org.example.configs.TestConfig;
import org.example.drivers.factories.WebDriverFactory;
import org.example.servers.TestServerInterface;
import org.example.servers.TestServerManager;
import org.example.tests.BaseTest;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;

import static org.example.constants.Settings.TEST_CONFIG_FILE_PATH;

public class CommonTest extends BaseTest {
    static private final TestConfig config = TestConfig.getInstance(TEST_CONFIG_FILE_PATH);

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.quiteAllBrowsersAndServers();
    }

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
        TestServerInterface testServer = TestServerManager.getTestServer();
        WebFormPageOutput actual = testServer.fillWebForm(input);
        WebFormPageOutput expected = new WebFormPageOutput();

        SmartAssert.assertDataObjects(expected, actual);
        Reporter.log("<b>fillWebForm test execution finished.</b>");
    }

    protected void submitWebForm() {
        Reporter.log("<b>submitWebForm test execution started.</b>");
        TestServerInterface testServer = TestServerManager.getTestServer();
        TargetPageOutput actual = testServer.submitWebForm();
        TargetPageOutput expected = new TargetPageOutput();

        SmartAssert.assertDataObjects(expected, actual);
        Reporter.log("<b>submitWebForm test execution finished.</b>");
    }
}
