package com.smarte2e.tests;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.balancers.LoadBalancer;
import com.smarte2e.configs.Config;
import com.smarte2e.ui.factories.WebDriverFactory;
import com.smarte2e.exceptions.SmartRuntimeException;
import com.smarte2e.helpers.RunAloneTestListener;
import com.smarte2e.utils.FileSystemUtils;
import com.smarte2e.utils.WebUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.smarte2e.constants.Settings.BLANK_PAGE_URL;
import static com.smarte2e.constants.Settings.SUREFIRE_REPORT_FOLDER_NAME;

@Slf4j
@Listeners(RunAloneTestListener.class)
public abstract class BaseTest {
    static private final String SCREENSHOTS_FOLDER_PATH = "./" + SUREFIRE_REPORT_FOLDER_NAME;
    static private final String VIDEOS_FOLDER_PATH = "./" + SUREFIRE_REPORT_FOLDER_NAME;
    static private final String DEFAULT_BROWSER_VERSION = "default";
    static private final Config config = Config.getInstance();

    @BeforeSuite()
    public void beforeSuite() {
/*        FileSystemUtils.deleteFolder(VIDEOS_FOLDER_PATH);
        FileSystemUtils.deleteFolder(SCREENSHOTS_FOLDER_PATH);*/
    }

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.quiteAllDriversAndServers();
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {

        try {
            LoadBalancer.getInstance().incrementServerThreadCount();

            if (method.isAnnotationPresent(Test.class)) {

                if (Config.getInstance().getRetainBrowser()) {
                    // Reset cookies and open blank page before every @Test method
                    WebDriver driver = WebDriverFactory.getDriver();
                    driver.manage().deleteAllCookies();
                    driver.get(BLANK_PAGE_URL);
                }
                if (config.getVideoOnFail()) {
                    startVideoRecording(method.getName());
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException("'Before' method failed.", e);
        }
    }

    @AfterMethod
    public void afterMethod(Method method, ITestResult result) {
        Reporter.setCurrentTestResult(result);
        LoadBalancer.getInstance().decrementServerThreadCount();
        int status = result.getStatus();

        try {
            if (method.isAnnotationPresent(Test.class)) {

                if (config.getScreenshotOnFail() && status == ITestResult.FAILURE) {
                    takeScreenshot(result);
                }
                if (config.getVideoOnFail()) {
                    WebDriverFactory.stopVideoRecording();

                    if (status != ITestResult.FAILURE) {
                        FileSystemUtils.deleteFile(WebDriverFactory.getVideoFilePath());
                    } else {
                        addVideoLinkToTestReport();
                    }
                }
                if (status == ITestResult.FAILURE && Config.getInstance().getDebugMode()) {
                    showDebugConfirm(result.getMethod().getQualifiedName(),
                            result.getThrowable().getMessage());
                }
                if (!Config.getInstance().getRetainBrowser()) {
                    // Quit browser and driver after every @Test method
                    WebDriverFactory.quitDriver();
                }
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException("'After' method failed.", e);
        }
    }

    private static void showDebugConfirm(String testName, String errorMessage) {
        String message = String.format("""
            TEST FAILURE
            
            Test method: %s
            Message: %s
            
            Press OK to continue.
            Or press CANCEL to terminate tests.
                        """.stripIndent(),
                testName, errorMessage);
        if (!WebUtils.showConfirm(message)) {
            log.info("User made hard system exit on test failure confirm popup.");
            WebDriverFactory.hardSystemExit();
        }
    }

    private static void takeScreenshot(ITestResult result) {
        String status = result.isSuccess() ? "success" : "failure";
        String browserName = config.getBrowserName().toString();
        String browserVersion = config.isBrowserVersionDefined() ?
                config.getBrowserVersion() : DEFAULT_BROWSER_VERSION;
        String methodName = result.getMethod().getMethodName();
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.SS.SSS").format(new Date());
        String filePath = String.format("%s/%s.%s.%s.%s.%s.png",
                SCREENSHOTS_FOLDER_PATH, status, browserName, browserVersion, methodName, timeStamp);

        WebDriverFactory.takeScreenshot(filePath);
        File file = new File(filePath);
        String relativePath = String.format("./%s", file.getName());
        Reporter.log(String.format("<br/><a href='%s'>Screenshot: %s</a>", relativePath, file.getName()));
        Reporter.log(String.format("<br/><img src='%s' width='600', height='400'/>", relativePath));
    }

    private static void startVideoRecording(String methodName) {
        String browserName = config.getBrowserName().toString();
        String browserVersion = config.isBrowserVersionDefined() ?
                config.getBrowserVersion() : DEFAULT_BROWSER_VERSION;
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.SS.SSS").format(new Date());
        String filePath = String.format("%s/failure.%s.%s.%s.%s.mp4",
                VIDEOS_FOLDER_PATH, browserName, browserVersion, methodName, timeStamp);

        FileSystemUtils.createFolder(VIDEOS_FOLDER_PATH);
        WebDriverFactory.enableVideoRecording(filePath);
        WebDriverFactory.startVideoRecording();
    }

    private void addVideoLinkToTestReport() {
        String filePath = WebDriverFactory.getVideoFilePath();
        File file = new File(filePath);
        String relativePath = String.format("./%s", file.getName());
        Reporter.log(String.format("<br/><a href='%s'>Video: %s</a>", relativePath, file.getName()));
        Reporter.log(String.format("<br/><video width='600' height='400' controls>" +
                "<source src='%s' type='video/mp4'></video>", relativePath));
    }
}
