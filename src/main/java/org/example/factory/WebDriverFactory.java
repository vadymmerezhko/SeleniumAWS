package org.example.factory;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.slf4j.Slf4j;
import org.example.balancer.LoadBalancer;
import org.example.data.Config;
import org.example.driver.robust.RobustWebDriver;
import org.example.enums.BrowserName;
import org.example.enums.TestMode;
import org.example.utils.*;
import org.example.driver.playwright.PlaywrightDriver;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.devicefarm.DeviceFarmClient;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlRequest;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlResponse;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.*;
import static org.example.enums.BrowserName.*;
import static org.example.enums.TestMode.*;

/**
 * The web driver factory class.
 */
@Slf4j
public class WebDriverFactory {
    static private final String SELENIUM_GRID_URL_TEMPLATE = "http://%S:4444";
    static private final String LOCALHOST = "localhost";
    static private final Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
    static private final ConcurrentMap<Long, WebDriver> driverMap = new ConcurrentHashMap<>();
    static private final int ADB_EXEC_TIMEOUT_SECONDS = 180000;
    private static final LoadBalancer loadBalancer = LoadBalancer.getInstance();
    private static boolean dockerSeleniumGridStarted = false;

    /**
     * Returns web driver instance.
     * The web driver type properties are defined with Config file and parameters.
     * @return The web driver instance.
     */
    public static WebDriver getDriver() {
        WebDriver driver;
        long threadId = Thread.currentThread().threadId();
        TestMode testMethod = config.getTestMode();
        BrowserName browserName = config.getBrowserName();
        String browserVersion = config.getBrowserVersion();
        int threadCount = config.getThreadCount();

        if (!driverMap.containsKey(threadId)) {

            log.info("Creating {} web driver for {}:{} browser...", testMethod, browserName, browserVersion);

            switch (testMethod) {
                case AWS_DOCKER -> driver = new RobustWebDriver(getAWSDockerDriver(
                        browserName, config.getBrowserVersion(), threadCount));
                case LOCAL_DOCKER -> driver = new RobustWebDriver(getLocalDockerWebDriver(
                        browserName, config.getBrowserVersion(), threadCount));
                case LOCAL ->  driver = new RobustWebDriver(getLocalWebDriver(browserName, browserVersion));
                case LOCAL_AUTO ->  driver = new RobustWebDriver(getLocalAutoWebDriver(browserName));
                case LOCAL_PLAYWRIGHT -> driver = getPlaywrightDriver(browserName);
                case REMOTE -> driver = new RobustWebDriver(getRemoteWebDriver(
                        config.getRemoteHost(), browserName, config.getBrowserVersion()));
                case AWS_DEVICE_FARM -> driver = new RobustWebDriver(getAWSDeviceFarmWebDriver(
                        browserName, config.getBrowserVersion()));
                case LOCAL_APPIUM -> driver = new RobustWebDriver(
                        getAppiumWebDriver(config.getEmulator((int)threadId % threadCount)));
                case LOCAL_ACCESSIBILITY -> driver = getPlaywrightDriver(
                        CHROMIUM, config.getHeadless(), true);
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

    /**
     * Returns local web driver by browser name and browser version.
     * @param browserName The browser name.
     * @param browserVersion The browser version (optional).
     * @return The web driver instance.
     */
    public static WebDriver getLocalWebDriver(BrowserName browserName, String browserVersion) {
        WebDriver driver;

        switch (browserName) {
            case CHROME -> driver = new ChromeDriver(getChromeOptions(browserVersion));
            case FIREFOX -> driver = new FirefoxDriver(getFirefoxOptions(browserVersion));
            case EDGE -> driver = new EdgeDriver(getEdgeOptions(browserVersion));
            default -> throw new RuntimeException("Unsupported browser: " + browserName);
        }
        return driver;
    }

    /**
     * Returns local auto web driver by browser name. Browser version is detected automatically.
     * @param browserName The browser name.
     * @return The web driver instance.
     */
    public static WebDriver getLocalAutoWebDriver(BrowserName browserName) {
        WebDriver driver;

        switch (browserName) {
            case CHROME -> driver = new ChromeDriver(getChromeOptions(null));
            case FIREFOX -> driver = new FirefoxDriver(getFirefoxOptions(null));
            case EDGE -> driver = new EdgeDriver(getEdgeOptions(null));
            case SAFARI -> driver = new SafariDriver(getSafariOptions());
            default -> throw new RuntimeException("Unsupported browser: " + browserName);
        }
        return driver;
    }

    /**
     * Returns local Docker web driver by browser name, browser version and thread count.
     * @param browserName The browser name.
     * @param browserVersion The browser version.
     * @param threadCount The maximal thread count.
     * @return The local Docker web driver instance.
     */
    private static WebDriver getLocalDockerWebDriver(BrowserName browserName, String browserVersion, int threadCount) {

        switch (browserName) {
            case CHROME, FIREFOX, EDGE -> {
                runSeleniumGridOnDocker(browserName, browserVersion, threadCount);
                return getRemoteWebDriver(String.format(SELENIUM_GRID_URL_TEMPLATE, LOCALHOST),
                        browserName, browserVersion);
            }
            default -> throw new RuntimeException("Unsupported Docker browser: " + browserName);
        }
    }

    /**
     * Returns AWS EC2 remote web driver by browser name, browser version and thread count.
     * @param browserName The browser name.
     * @param browserVersion The browser version.
     * @param threadCount The maximal thread count.
     * @return The remote web driver instance.
     */
    private static WebDriver getAWSDockerDriver(BrowserName browserName, String browserVersion, int threadCount) {
            long serverId = loadBalancer.getThreadServerId();
            String ec2InstanceIp = loadBalancer.getServerPublicIp(
                    serverId, threadCount, browserName, browserVersion);
            try {
                log.info("Waiting for AWS EC2 instance...");
                ServerManager.waitForServerAvailability(ec2InstanceIp, REMOTE_WEB_DRIVER_PORT);
            }
            catch (Exception e) {
                log.error("Wait Selenium Grid error!");
                loadBalancer.lockSever(serverId);
                return getDriver();
            }

        return getRemoteWebDriver(
                String.format(SELENIUM_GRID_URL_TEMPLATE, ec2InstanceIp),
                browserName, config.getBrowserVersion());
    }

    private static WebDriver getPlaywrightDriver(BrowserName browserName) {
        return getPlaywrightDriver(browserName, config.getHeadless(), false);
    }

    /**
     * Returns local Playwright web driver by browser name.
     * @param browserName The browser name.
     * @return The Playwright web driver instance.
     */
    private static WebDriver getPlaywrightDriver(
            BrowserName browserName, boolean headless, boolean accessibilityTest) {
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
            PlaywrightDriver driver = new PlaywrightDriver(browser);
            driver.setAccessibilityTestEnabled(accessibilityTest);
            return driver;
        }
        catch (Exception e) {
            throw new RuntimeException("Playwright driver exception:\n" + e.getMessage());
        }
    }

    /**
     * Returns remote web driver by browser name and browser version.
     * @param remoteHost The remote host URL in format like: http://<IP>:<port>.
     * @param browserName The browser name.
     * @param browserVersion The browser version (optional).
     * @return The remote web driver instance.
     */
    private static WebDriver getRemoteWebDriver(String remoteHost, BrowserName browserName, String browserVersion) {
        Capabilities options;
        URI uri;
        try {
            uri = new URI(remoteHost);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        // Wait for Selenium remote server.
        ServerManager.waitForServerAvailability(uri.getHost(), uri.getPort());

        switch (browserName) {
            case CHROME -> options = getChromeOptions(browserVersion);
            case FIREFOX -> options = getFirefoxOptions(browserVersion);
            case EDGE -> options = getEdgeOptions(browserVersion);
            default -> throw new RuntimeException("Unsupported browser: " + browserName);
        }

        try {
            return new RemoteWebDriver(uri.toURL(), options);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns AWS Device Farm web driver by browser name and browser version.
     * @param browserName The browser name.
     * @param browserVersion The browser version (optional).
     * @return The AWS Device Farm web driver instance.
     */
    private static WebDriver getAWSDeviceFarmWebDriver(BrowserName browserName, String browserVersion) {
        WebDriver driver = null;
        URL testGridUrl;
        String awsDeviceFarmBrowserName = browserName.toString();
        DesiredCapabilities capabilities = new DesiredCapabilities();

        if (browserName == EDGE) {
            awsDeviceFarmBrowserName = "MicrosoftEdge";
            capabilities.setCapability("ms:edgeChromium", true);
        }

        capabilities.setCapability("browserName", awsDeviceFarmBrowserName);
        capabilities.setCapability("browserVersion", browserVersion);

        try {
            log.info("Waiting for AWS Device Farm browser: {}", browserName);
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
            log.error("AWS Device Farm exception:\n{}", e.getMessage());
            WebDriverFactory.closeAllDrivers();
            ServerManager.terminateAllSeleniumServers();
            System.exit(-1);
        }
        return driver;
    }

    /**
     * Returns Appium web driver by emulator name.
     * @param emulatorName The emulator name.
     * @return The Appium web driver instance.
     */
    private static WebDriver getAppiumWebDriver(String emulatorName) {
        try {
            String deviceName = AppiumManager.getDeviceName(emulatorName);
            String platformName = AppiumManager.getPlatformName(emulatorName);
            String platformVersion = AppiumManager.getPlatformVersion(emulatorName);
            BrowserName browserName = AppiumManager.getBrowserName(emulatorName);
            String browserVersion = AppiumManager.getBrowserVersion(emulatorName);
            String chromeDriverPath = BrowserManager.downloadWebDriverBinary(browserName, browserVersion);

            URL appiumServiceUrl = AppiumManager.startAppiumServer(config.getThreadCount());

            log.info("Starting emulator {}...", emulatorName);
            UiAutomator2Options options = new UiAutomator2Options();
            options.setPlatformName(platformName);
            options.setPlatformVersion(platformVersion);
            options.setCapability("avd", deviceName);
            options.setDeviceName(deviceName);
            options.setCapability(CapabilityType.BROWSER_NAME, browserName);
            options.setCapability("chromedriverExecutable", chromeDriverPath);
            options.setCapability("adbExecTimeout", ADB_EXEC_TIMEOUT_SECONDS);
            options.setCapability("noReset", "true");
            options.setCapability("maxInstances", config.getThreadCount());

            return new AppiumDriver(appiumServiceUrl, options);
        }
        catch (Exception e) {
            AppiumManager.stopAppiumServer();
            throw new RuntimeException("Cannot get Appium WebDriver:\n" + e.getMessage());
        }
    }

    private static ChromeOptions getChromeOptions(String browserVersion) {
        ChromeOptions options = new ChromeOptions();

        if (config.getTestMode() == LOCAL) {
            String chromeDriverPath = BrowserManager.downloadWebDriverBinary(CHROME, browserVersion);
            String chromeBrowserPath = BrowserManager.downloadBrowserBinary(CHROME, browserVersion);

            System.setProperty("webdriver.chrome.driver", chromeDriverPath);
            options.setBinary(chromeBrowserPath);
        }
        else if (config.getTestMode() == LOCAL_AUTO) {
            WebDriverManager.chromedriver().clearDriverCache().setup();
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

    private static FirefoxOptions getFirefoxOptions(String browserVersion) {
        FirefoxOptions options = new FirefoxOptions();

        if (config.getTestMode().equals(LOCAL)) {
            String geckoDriverPath = BrowserManager.downloadWebDriverBinary(FIREFOX, browserVersion);
            System.setProperty("webdriver.chrome.driver", geckoDriverPath);
        }
        else if (config.getTestMode().equals(LOCAL_AUTO)) {
            WebDriverManager.firefoxdriver().clearDriverCache().setup();
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

    private static EdgeOptions getEdgeOptions(String browserVersion) {
       EdgeOptions options = new EdgeOptions();

        if (config.getTestMode().equals(LOCAL)) {
            String edgeDriverPath = BrowserManager.downloadWebDriverBinary(EDGE, browserVersion);
            System.setProperty("webdriver.chrome.driver", edgeDriverPath);
        }
        else if (config.getTestMode().equals(LOCAL_AUTO)) {
            WebDriverManager.edgedriver().clearDriverCache().setup();
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

    private static SafariOptions getSafariOptions() {
        SafariOptions options = new SafariOptions();
        WebDriverManager.safaridriver().clearDriverCache().setup();
        return options;
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
            log.error("Cannot close all drivers:\n{}", e.getMessage());
        }

        if (config.getTestMode() == LOCAL_APPIUM) {
            AppiumManager.stopAllAppiumServers();
        }
    }

    private static Thread closeDriverInParallel(long threadId) {
        Thread thread = new Thread(() -> WebDriverFactory.closeDriver(threadId));
        thread.start();
        return thread;
    }

    synchronized private static void runSeleniumGridOnDocker(
            BrowserName browserName, String browserVersion, int threadCount) {

        if (!dockerSeleniumGridStarted) {
            stopSeleniumGridOnDocker();
            log.info("Starting {}:{} Selenium Standalone on Docker...", browserName, browserVersion);
            DockerManager.runSeleniumStandalone(browserName, browserVersion, threadCount);
            ServerManager.waitForServerAvailability(LOCALHOST, REMOTE_WEB_DRIVER_PORT);
            dockerSeleniumGridStarted = true;
        }
    }

    synchronized private static void stopSeleniumGridOnDocker() {
        log.info("Stopping Selenium Grid on Docker...");
        DockerManager.stopAllContainers();
        DockerManager.removeAllContainers();
        dockerSeleniumGridStarted = false;
    }
}
