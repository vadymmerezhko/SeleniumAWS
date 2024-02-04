package org.example.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.example.balancer.LoadBalancer;
import org.example.data.Config;
import org.example.driver.robust.RobustWebDriver;
import org.example.utils.*;
import org.example.driver.playwright.PlaywrightDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.devicefarm.DeviceFarmClient;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlRequest;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlResponse;

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Browsers.*;
import static org.example.constants.Settings.*;
import static org.example.constants.TestModes.*;

public class WebDriverFactory {
    static private final String SELENIUM_GRID_URL_TEMPLATE = "http://%S:4444";
    static private final String LOCALHOST = "localhost";
    static private final Config config = new Config("config.properties");
    static private final ConcurrentMap<Long, WebDriver> driverMap = new ConcurrentHashMap<>();
    static private final int WAIT_SELENIUM_GRID_TIMEOUT = 30;
    private static final LoadBalancer loadBalancer = LoadBalancer.getInstance();
    private static boolean dockerSeleniumGridStarted = false;

    public static WebDriver getDriver() {
        WebDriver driver;
        long threadId = Thread.currentThread().threadId();
        String testMethod = config.getTestMode();
        String browserName = config.getBrowserName();
        int threadCount = config.getThreadCount();

        loadBalancer.incrementServerThreadCount();
        System.out.println("Thread Id: " + threadId);

        if (!driverMap.containsKey(threadId)) {

            switch (testMethod) {
                case AWS_DOCKER -> driver = new RobustWebDriver(getAWSDockerDriver(
                        browserName, config.getBrowserVersion(), threadCount));
                case LOCAL_DOCKER -> driver = new RobustWebDriver(getLocalDockerWebDriver(
                        browserName, config.getBrowserVersion(), threadCount));
                case LOCAL ->  driver = new RobustWebDriver(getLocalWebDriver(browserName, config.getBrowserVersion()));
                case LOCAL_PLAYWRIGHT -> driver = getPlaywrightDriver(browserName);
                case REMOTE -> driver = new RobustWebDriver(getRemoteWebDriver(
                        config.getRemoteHost(), browserName, config.getBrowserVersion()));
                case AWS_DEVICE_FARM -> driver = new RobustWebDriver(getAWSDeviceFarmWebDriver(
                        browserName, config.getBrowserVersion()));
                default -> throw new RuntimeException("Unsupported test mode: " + testMethod);
            }
            driverMap.put(threadId, driver);
        }
        else {
            driver = driverMap.get(threadId);
            driver.manage().deleteAllCookies();
        }
        return driver;
    }

    public static WebDriver getLocalWebDriver(String browserName, String browserVersion) {
        WebDriver driver;

        switch (browserName) {
            case CHROME -> driver = new ChromeDriver(getChromeOptions(browserName, browserVersion));
            case FIREFOX -> driver = new FirefoxDriver(getFirefoxOptions(browserName, browserVersion));
            case EDGE -> driver = new EdgeDriver(getEdgeOptions(browserName, browserVersion));
            default -> throw new RuntimeException("Unsupported browser: " + browserName);
        }
        return driver;
    }

    private static WebDriver getLocalDockerWebDriver(String browserName, String browserVersion, int threadCount) {

        switch (browserName) {
            case CHROME, FIREFOX, EDGE -> {
                runSeleniumGridOnDocker(browserName, browserVersion, threadCount);
                return new RobustWebDriver(getRemoteWebDriver(
                        String.format(SELENIUM_GRID_URL_TEMPLATE, LOCALHOST),
                        browserName, browserVersion));
            }
            default -> throw new RuntimeException("Unsupported Docker browser: " + browserName);
        }
    }

    private static WebDriver getAWSDockerDriver(String browserName, String browserVersion, int threadCount) {
            long serverId = loadBalancer.getThreadServerId();
            String ec2InstanceIp = loadBalancer.getServerPublicIp(
                    serverId, threadCount, browserName, browserVersion);
            try {
                System.out.println("Waiting for AWS EC2 instance...");
                waitForSeleniumGrid(String.format(SELENIUM_GRID_URL_TEMPLATE, ec2InstanceIp));
            } catch (Exception e) {
                System.out.println("Wait Selenium Grid timeout expired!");
                loadBalancer.lockSever(serverId);
                return getDriver();
            }

        return getRemoteWebDriver(
                String.format(SELENIUM_GRID_URL_TEMPLATE, ec2InstanceIp),
                browserName, config.getBrowserVersion());
    }

    private static WebDriver getPlaywrightDriver(String browserName) {
        return getPlaywrightDriver(browserName, config.getHeadless());
    }

    private static WebDriver getPlaywrightDriver(String browserName, boolean headless) {
        Playwright playwright = Playwright.create();
        BrowserType browserType;

        try {
            switch (browserName) {
                case CHROMIUM -> browserType = playwright.chromium();
                case FIREFOX -> browserType = playwright.firefox();
                case WEBKIT -> browserType = playwright.webkit();
                default -> throw new RuntimeException("Unsupported Playwright browser: " + browserName);
            }

            Browser browser = browserType.launch(
                    new BrowserType.LaunchOptions()
                    .setHeadless(headless)
                    .setSlowMo(0));
            return new PlaywrightDriver(browser);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Playwright driver exception:\n" + e.getMessage());
        }
    }

    private static WebDriver getRemoteWebDriver(String remoteHost, String browserName, String browserVersion) {
        WebDriver driver = null;
        Capabilities options;

        switch (browserName) {
            case CHROME -> options = getChromeOptions(browserName, browserVersion);
            case FIREFOX -> options = getFirefoxOptions(browserName, browserVersion);
            case EDGE -> options = getEdgeOptions(browserName, browserVersion);
            default -> throw new RuntimeException("Unsupported browser: " + browserName);
        }

        int repeatCount = 3;

        while (repeatCount > 0) {
            try {
                URI uri = new URI(remoteHost);
                driver = new RemoteWebDriver(uri.toURL(), options);
                return driver;
            } catch (Exception e) {
                repeatCount--;
                Waiter.waitSeconds(1);
            }
        }
        return driver;
    }

    private static WebDriver getAWSDeviceFarmWebDriver(String browserName, String browserVersion) {
        WebDriver driver = null;
        URL testGridUrl;
        DesiredCapabilities capabilities = new DesiredCapabilities();

        if (browserName.equals(EDGE)) {
            browserName = "MicrosoftEdge";
            capabilities.setCapability("ms:edgeChromium", true);
        }

        capabilities.setCapability("browserName", browserName);
        capabilities.setCapability("browserVersion", browserVersion);

        try {
            DeviceFarmClient client = DeviceFarmClient.builder().region(Region.US_WEST_2).build();
            CreateTestGridUrlRequest request = CreateTestGridUrlRequest.builder()
                    .expiresInSeconds(AWS_URL_EXPIRES_SECONDS)
                    .projectArn(AWS_DEVICE_FARM_BROWSERS_ARM)
                    .build();
            CreateTestGridUrlResponse response = client.createTestGridUrl(request);
            URI uri = new URI(response.url());
            testGridUrl = uri.toURL();
            driver = new RemoteWebDriver(testGridUrl, capabilities);
        }
        catch (Exception e) {
            System.out.println("AWS Device Farm exception:\n" + e.getMessage());
            WebDriverFactory.closeAllDrivers();
            ServerManager.terminateAllSeleniumServers();
            System.exit(-1);
        }
        return driver;
    }

    private static ChromeOptions getChromeOptions(String browserName, String browserVersion) {
        ChromeOptions options = new ChromeOptions();

        if (config.getTestMode().equals(LOCAL)) {
            String chromeDriverPath = BrowserManager.downloadWebDriverBinary(browserName, browserVersion);
            String chromeBrowserPath = BrowserManager.downloadBrowserBinary(browserName, browserVersion);

            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            options.setBinary(chromeBrowserPath);
        }

        options.addArguments("--disable-gpu"); // applicable to Windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // bypass OS security model
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("disable-infobars"); // disabling infobars

        if (config.getHeadless()) {
            options.addArguments("--headless"); // headless only
        }
        return options;
    }

    private static FirefoxOptions getFirefoxOptions(String browserName, String browserVersion) {
        FirefoxOptions options = new FirefoxOptions();

        if (config.getTestMode().equals(LOCAL)) {
            String geckoDriverPath = BrowserManager.downloadWebDriverBinary(browserName, browserVersion);
            System.setProperty("webdriver.chrome.driver", geckoDriverPath);
        }

        options.addArguments("--disable-gpu"); // applicable to Windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // bypass OS security model
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("disable-infobars"); // disabling infobars

        if (config.getHeadless()) {
            options.addArguments("--headless"); // headless only
        }
        return options;
    }

    private static EdgeOptions getEdgeOptions(String browserName, String browserVersion) {
       EdgeOptions options = new EdgeOptions();

        if (config.getTestMode().equals(LOCAL)) {
            String edgeDriverPath = BrowserManager.downloadWebDriverBinary(browserName, browserVersion);
            System.setProperty("webdriver.chrome.driver", edgeDriverPath);
        }

        options.addArguments("--disable-gpu"); // applicable to Windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // bypass OS security model
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("disable-infobars"); // disabling infobars
        options.setExperimentalOption("excludeSwitches", List.of("disable-popup-blocking"));

        if (config.getHeadless()) {
            options.addArguments("--headless"); // headless only
        }
        return options;
    }

    private static void waitForSeleniumGrid(String ec2InstanceHost) {
        WebDriver tempDriver = getPlaywrightDriver(CHROMIUM, true);
        TimeOut timeOut = new TimeOut(WAIT_SELENIUM_GRID_TIMEOUT);
        timeOut.start();

        try {
            while (true) {
                try {
                    tempDriver.get(ec2InstanceHost);

                    if (!tempDriver.findElements(
                            By.xpath("//*[contains(., 'Selenium Grid')]"))
                            .isEmpty()) {
                        return;
                    }
                } catch (Exception e) {
                    //NOP
                }
                Waiter.waitSeconds(5);
            }
        }
        finally {
            tempDriver.quit();
        }
    }

    public static void closeDriver() {
        long threadId = Thread.currentThread().threadId();
        closeDriver(threadId);
    }

    private static void closeDriver(long threadId) {
        if (driverMap.containsKey(threadId)) {
            driverMap.get(threadId).quit();
            driverMap.remove(threadId);
        }
    }

    public static void closeAllDrivers() {
        Set<Thread> threadSet = new HashSet<>();

        try {
            for (Long threadId : driverMap.keySet()) {
                threadSet.add(closeDriverInParallel(threadId));
            }
            // Wait for closing all drivers.
            for (Thread thread : threadSet) {
                thread.join();
            }
        }
        catch (Exception e) {
            System.out.println("Ca not close all drivers:\n" + e.getMessage());
        }
    }

    private static Thread closeDriverInParallel(long threadId) {
        Thread thread = new Thread(() -> WebDriverFactory.closeDriver(threadId));
        thread.start();
        return thread;
    }

    synchronized private static void runSeleniumGridOnDocker(
            String browserName, String browserVersion, int threadCount) {

        if (!dockerSeleniumGridStarted) {
            stopSeleniumGridOnDocker();
            System.out.println("Starting Selenium Standalone on Docker.");
            System.out.println(DockerManager.runSeleniumStandalone(browserName, browserVersion, threadCount));
            waitForSeleniumGrid(String.format(SELENIUM_GRID_URL_TEMPLATE, LOCALHOST));
            dockerSeleniumGridStarted = true;
        }
    }

    synchronized private static void stopSeleniumGridOnDocker() {
        System.out.println("Stopping Selenium Grid on Docker.");
        System.out.println(DockerManager.stopAllContainers());
        System.out.println(DockerManager.removeAllContainers());
        dockerSeleniumGridStarted = false;
    }
}
