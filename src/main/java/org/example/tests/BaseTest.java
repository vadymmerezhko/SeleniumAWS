package org.example.tests;

import org.example.balancers.LoadBalancer;
import org.example.configs.Config;
import org.example.drivers.elements.BaseElement;
import org.example.drivers.factories.WebDriverFactory;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.FileSystemUtils;
import org.example.utils.WebUtils;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class BaseTest {
    static private final String SCREENSHOTS_FOLDER_PATH = "./target/surefire-reports/screenshots";
    static private final String VIDEOS_FOLDER_PATH = "./target/surefire-reports/videos";
    static private final String DEFAULT_BROWSER_VERSION = "default";
    static private final Config config = Config.getInstance();

    @BeforeSuite
    public void beforeSuite() {
        WebUtils.readAllElementSelectorsFromFiles(
                config.getPagesFolderPath(),
                BaseElement.getElementSelectorMap());
        FileSystemUtils.deleteFolder(VIDEOS_FOLDER_PATH);
        FileSystemUtils.deleteFolder(SCREENSHOTS_FOLDER_PATH);
    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(ITestResult result) {
        LoadBalancer.getInstance().incrementServerThreadCount();
        WebDriverFactory.getDriver().manage().deleteAllCookies();
        WebDriverFactory.getDriver().navigate().refresh();

        try {
            if (config.getVideoOnFail()) {
                startVideoRecording(result.getMethod().getMethodName());
            }
        }
           catch (Exception e) {
            throw new SmartRuntimeException("'Before' method failed.", e);
        }
    }

    @AfterMethod
    public void afterMethod(ITestResult result) {
        Reporter.setCurrentTestResult(result);
        LoadBalancer.getInstance().decrementServerThreadCount();
        int status = result.getStatus();

        try {
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
                showDebugAlert(result.getMethod().getQualifiedName(),
                        result.getThrowable().getMessage());
            }
            if (!Config.getInstance().getRetainBrowser()) {
                WebDriverFactory.quitDriver();
            }
        }
        catch (Exception e) {
            throw new SmartRuntimeException("'After' method failed.", e);
        }
    }

    private static void showDebugAlert(String testName, String errorMessage) {
        String message = String.format(
                "TEST FAILURE\n\nTest '%s' has failed.\n" +
                "Error: %s\n\n" +
                "Press OK to continue.\n" +
                "Or press CANCEL to terminate tests.",
                testName, errorMessage);
        if (!WebUtils.showConfirm(message)) {
            WebDriverFactory.terminateAllBrowsersAndServers();
            System.exit(-1);
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
        String relativePath = String.format("./screenshots/%s", file.getName());
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
        String relativePath = String.format("./videos/%s", file.getName());
        Reporter.log(String.format("<br/><a href='%s'>Video: %s</a>", relativePath, file.getName()));
        Reporter.log(String.format("<br/><video width='600' height='400' controls>" +
                "<source src='%s' type='video/mp4'></video>", relativePath));
    }
}
