package org.example;

import org.example.balancer.LoadBalancer;
import org.example.data.Config;
import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;
import org.example.factory.WebDriverFactory;
import org.example.server.TestServerInterface;
import org.example.server.TestServerManager;
import org.example.utils.FileManager;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.example.constants.Settings.CONFIG_PROPERTIES_FILE_NAME;

public class BaseTest {
    static private final String SCREENSHOTS_FOLDER_PATH = "./screenshots";
    static private final String VIDEOS_FOLDER_PATH = "./videos";
    static private final Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);


    @BeforeSuite
    public void beforeSuite() {
        FileManager.deleteFolder(SCREENSHOTS_FOLDER_PATH);
        FileManager.deleteFolder(VIDEOS_FOLDER_PATH);
        FileManager.createFolder(VIDEOS_FOLDER_PATH);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestResult result) {
        LoadBalancer.getInstance().incrementServerThreadCount();

        if (config.getVideoOnFail()) {
            enableVideoRecording(result);
        }
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        LoadBalancer.getInstance().decrementServerThreadCount();

        if (config.getScreenshotOnFail() && result.getStatus() == ITestResult.FAILURE) {
            takeScreenshot(result);
        }

        if (config.getVideoOnFail()) {
            WebDriverFactory.stopVideoRecording();

            if (!(result.getStatus() == ITestResult.FAILURE)) {
                FileManager.deleteFile(WebDriverFactory.getVideoFilePath());
            }
        }
    }

    protected void signUp() {
        Path currentRelativePath = Paths.get("pom.xml");
        String currentFolderPath = currentRelativePath.toAbsolutePath().toString();

        SignUpTestInput testInput = new SignUpTestInput(
                "Selenium",
                "Selenium WebDriver",
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
    }

    private static void takeScreenshot(ITestResult result) {
        String status = result.isSuccess() ? "success" : "failure";
        String browserName = config.getBrowserName().toString();
        String browserVersion = config.getBrowserVersion();
        String methodName = result.getMethod().getMethodName();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.SS.SSS").format(new Date());
        String filePath = String.format("%s/%s.%s.%s.%s.%s.png",
                SCREENSHOTS_FOLDER_PATH, status, browserName, browserVersion, methodName, timeStamp);

        WebDriverFactory.takeScreenshot(filePath);
    }

    private static void enableVideoRecording(ITestResult result) {
        String browserName = config.getBrowserName().toString();
        String browserVersion = config.getBrowserVersion();
        String methodName = result.getMethod().getMethodName();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.SS.SSS").format(new Date());
        String filePath = String.format("%s/failure.%s.%s.%s.%s.mp4",
                VIDEOS_FOLDER_PATH, browserName, browserVersion, methodName, timeStamp);

        WebDriverFactory.enableVideoRecording(filePath);
    }
}
