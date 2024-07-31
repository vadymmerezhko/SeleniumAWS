package org.example.e2e;

import org.example.data.FillWebFormTestInput;
import org.example.data.FillWebFormTestResult;
import org.example.configs.TestConfig;
import org.example.data.SubmitWebFormTestResult;
import org.example.drivers.factories.WebDriverFactory;
import org.example.servers.TestServerInterface;
import org.example.servers.TestServerManager;
import org.example.tests.BaseTest;
import org.example.utils.ServerUtils;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.annotations.AfterSuite;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CommonTest extends BaseTest {
    static private final TestConfig config = TestConfig.getInstance();

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.quitAllDrivers();
        ServerUtils.terminateAllSeleniumServers();
        ServerUtils.terminateAwsRmiServer();
    }

    protected void fillWebForm() {
        Path currentRelativePath = Paths.get("pom.xml");
        String currentFolderPath = currentRelativePath.toAbsolutePath().toString();
        FillWebFormTestInput testInput = new FillWebFormTestInput(
                "Selenium",
                "Selenium WebDriver", // Multiline text cause failure on Safari.
                "Two",
                "Chicago",
                currentFolderPath,
                false,
                true,
                false,
                true,
                "#0088ff",
                "05/23/1970",
                2);

        fillWebForm(testInput);
    }

    protected void failFillWebForm() {
        fillWebForm();
        if (config.getDebugFail()) {
            Assert.fail("Test is failed for debug purpose.");
        }
    }

    protected void submitWebForm() {
        TestServerInterface testServer = TestServerManager.getTestServer();
        SubmitWebFormTestResult testResult = testServer.submitWebForm();

        Assert.assertEquals(testResult.header(), "Form submitted");
        Assert.assertEquals(testResult.status(), "Received!");
    }

    private void fillWebForm(FillWebFormTestInput testInput) {
        Reporter.log("<b>fillWebForm test execution started.</b>");

        TestServerInterface testServer = TestServerManager.getTestServer();
        FillWebFormTestResult testResult = testServer.fillWebForm(testInput);

        Assert.assertEquals(testResult.textInput(), testInput.textInput());
        Assert.assertEquals(testResult.textareaInput(), testInput.textareaInput());
        Assert.assertEquals(testResult.dropdownSelectedOption(), testInput.dropdownSelectedOption());
        Assert.assertEquals(testResult.dataListSelectOption(), testInput.dataListSelectOption());
        // TODO: Fix file path for remote run.
        //Assert.assertTrue((testResult.filePath().contains("pom.xml")));
        //TODO: fix checkbox value for Android
        Assert.assertEquals(testResult.checkbox1Value(), testInput.checkbox1Value());
        Assert.assertEquals(testResult.radiobutton1Value(), testInput.radiobutton1Value());
        Assert.assertEquals(testResult.radiobutton2Value(), testInput.radiobutton2Value());
        Assert.assertEquals(testResult.color(), testInput.color());
        Assert.assertEquals(testResult.date(), testInput.date());
        Assert.assertEquals(testResult.range(), testInput.range());

        Reporter.log("<b>fillWebForm test execution finished.</b>");
    }
}
