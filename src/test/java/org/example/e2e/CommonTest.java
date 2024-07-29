package org.example.e2e;

import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;
import org.example.configs.TestConfig;
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

    protected void signUp() {
        Path currentRelativePath = Paths.get("pom.xml");
        String currentFolderPath = currentRelativePath.toAbsolutePath().toString();
        SignUpTestInput testInput = new SignUpTestInput(
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

        signUp(testInput);
    }

    protected void failSignUp() {
        signUp();
        if (config.getDebugFail()) {
            Assert.fail("Test is failed for debug purpose.");
        }
    }

    private void signUp(SignUpTestInput testInput) {
        Reporter.log("<b>SignUp test execution started.</b>");

        TestServerInterface testServer = TestServerManager.getTestServer();
        SignUpTestResult testResult = testServer.signUp(testInput);

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

        Reporter.log("<b>SignUp test execution finished.</b>");
    }
}
