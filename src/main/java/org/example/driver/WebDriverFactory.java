package org.example.driver;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Playwright;
import org.example.balancer.LoadBalancer;
import org.example.driver.robust.RobustWebDriver;
import org.example.utils.TimeOut;
import org.example.utils.Waiter;
import org.example.driver.playwright.PlaywrightDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.devicefarm.DeviceFarmClient;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlRequest;
import software.amazon.awssdk.services.devicefarm.model.CreateTestGridUrlResponse;


import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.Settings.*;

public class WebDriverFactory {
    static private final ConcurrentMap<Long, WebDriver> driverMap = new ConcurrentHashMap<>();
    static private final int WAIT_SELENIUM_GRID_TIMEOUT = 30;
    private static final LoadBalancer loadBalancer = LoadBalancer.getInstance();

    public static WebDriver getDriver() {
        WebDriver driver;

        long threadId = Thread.currentThread().threadId();
        //System.out.println("Thread: " + threadId);

        int threadCount = getThreadCount();
        /*String ec2InstanceIp;
        loadBalancer.incrementServerThreadCount();
        long serverId = loadBalancer.getThreadServerId();
        System.out.println("Serer Id: " + serverId);*/

        if (!driverMap.containsKey(threadId)) {
/*          ec2InstanceIp = loadBalancer.getServerPublicIp(serverId);
            try {
                waitForSeleniumGrid(ec2InstanceIp);
            }
            catch (TimeOutException e) {
                System.out.println("Wait Selenium Grid timeout!");
                loadBalancer.lockSever(serverId);
                return getDriver();
            }*/

            //driver = new RobustWebDriver(getRemoteWebDriver(ec2InstanceIp));
            driver = new RobustWebDriver(getRemoteWebDriver("localhost"));
            //driver = new RobustWebDriver(getLocalWebDriver());
            //driver = getPlaywrightDriver();
            //driver = getLocalWebDriver();
/*            driver = new RobustWebDriver(getAWSRemoteWebDriver(
                    AWS_DEVICE_FARM_BROWSERS_ARM, // AWS_DEVICE_FARM_BROWSER_PROJECT_ARN[(int)threadId % AWS_DEVICE_FARM_BROWSER_PROJECT_ARN.length],
                    AWS_DEVICE_FARM_BROWSERS[(int)threadId % AWS_DEVICE_FARM_BROWSERS.length],
                   AWS_DEVICE_FARM_BROWSER_VERSIONS[(int)threadId % AWS_DEVICE_FARM_BROWSER_VERSIONS.length]));*/
            driverMap.put(threadId, driver);
            //System.out.println("Tread " + threadId + " driver is set up!!!");
        }
        else {
            driver = driverMap.get(threadId);
            //driver.manage().deleteAllCookies();
        }
        return driver;
    }

    private static int getThreadCount() {
        try {
            String threadCount = System.getProperty("threadCount");
            return Integer.parseInt(threadCount);
        }
        catch (Exception e) {
            return DEFAULT_THREADS_COUNT;
        }
    }

    public static WebDriver getLocalWebDriver() {
        String osName = System.getProperty("os.name");
        String chromeDriverPath;
        String chromeBrowserPath;

        if (osName != null && osName.startsWith("Windows")) {
            chromeDriverPath = "C:\\Selenium\\chromedriver.exe";
            chromeBrowserPath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe";
        } else {
            chromeDriverPath = "/tmp/bin/chromedriver-linux64/chromedriver";
            chromeBrowserPath = "/tmp/bin/chrome-linux64/chrome";
        }

        System.setProperty("webdriver.chrome.driver", chromeDriverPath);

        ChromeOptions options = new ChromeOptions();
        options.setBinary(chromeBrowserPath);
        options.addArguments("--headless"); // headless only
        options.addArguments("--disable-gpu"); // applicable to Windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // bypass OS security model
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("disable-infobars"); // disabling infobars
        return new ChromeDriver(options);
    }

    private static WebDriver getPlaywrightDriver() {
        try {
            Playwright playwright = Playwright.create();
            Browser browser = playwright.chromium().launch(//);
                    new BrowserType.LaunchOptions()
                    .setHeadless(false)
                    .setSlowMo(0));
            return new PlaywrightDriver(browser);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static WebDriver getRemoteWebDriver(String ec2InstanceIp) {
        WebDriver driver = null;
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // headless only
        options.addArguments("--disable-gpu"); // applicable to Windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // bypass OS security model
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("disable-infobars"); // disabling infobars
        //driver = new ChromeDriver(options);

        int repeatCount = 2;
        Exception exception = null;

        while (repeatCount > 0) {
            try {
                driver = new RemoteWebDriver(new URL("http://" + ec2InstanceIp + ":4444"), options);
                driver.manage().window().maximize();
                //System.out.println("Session created");
                return driver;
            } catch (Exception e) {
                exception = e;
                repeatCount--;
                Waiter.waitSeconds(1);
                //System.out.println("Repeat getting WebDriver");
            }
        }
        //System.out.println(exception.getMessage());
        return driver;
    }

    private static WebDriver getAWSRemoteWebDriver(String arn, String browser, String browserVersion) {
        WebDriver driver = null;
        URL testGridUrl;
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setCapability("browserName", browser);
        capabilities.setCapability("browserVersion", browserVersion);
/*        capabilities.setCapability("platform", "windows");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // headless only
        options.addArguments("--disable-gpu"); // applicable to Windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // bypass OS security model
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("disable-infobars"); // disabling infobars*/

        try {
            DeviceFarmClient client = DeviceFarmClient.builder().region(Region.US_WEST_2).build();
            CreateTestGridUrlRequest request = CreateTestGridUrlRequest.builder()
                    .expiresInSeconds(300)
                    .projectArn(arn)
                    .build();
            CreateTestGridUrlResponse response = client.createTestGridUrl(request);
            testGridUrl = new URL(response.url());
            // You can now pass this URL into RemoteWebDriver.
            driver = new RemoteWebDriver(testGridUrl, capabilities);
            //System.out.println("AWS " + browser + " " + browserVersion + " browser is set up!!!");
        }
        catch (Exception e) {
            //System.out.println("AWS Device Farm exception:\n" + e.getMessage());
            WebDriverFactory.closeAllDrivers();
            System.exit(-1);
            //throw new RuntimeException(e);
        }
        return driver;
    }

    private static void waitForSeleniumGrid(String ec2InstanceIp) {
        System.setProperty("webdriver.chrome.driver", "c:\\Selenium\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // headless only
        options.addArguments("--disable-gpu"); // applicable to Windows os only
        WebDriver tempDriver = new ChromeDriver(options);
        TimeOut timeOut = new TimeOut(WAIT_SELENIUM_GRID_TIMEOUT);
        timeOut.start();

        try {
            while (true) {
                try {
                    tempDriver.get("http://" + ec2InstanceIp + ":4444");

                    if (!tempDriver.findElements(
                            By.xpath("//*[contains(text(), 'Selenium Grid')]"))
                            .isEmpty()) {
                        return;
                    }
                } catch (Exception e) {
                    //NOP
                }
                //System.out.println("Waiting for: " + ec2InstanceIp);
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
        long startMilliSec = System.currentTimeMillis();
        //System.out.println("Start closing all drivers.");
        Set<Thread> threadSet = new HashSet<>();

        try {
            //driverMap.keySet().forEach(WebDriverFactory::closeDriver);
            driverMap.keySet().forEach(threadId -> {
                threadSet.add(closeDriverInParallel(threadId));
            });
            //System.out.println("Wait for closing all drivers.");
            for (Thread thread : threadSet) {
                thread.join();
            }
        }
        catch (Exception e) {
            System.out.println("Exception when closing all drivers:\n" + e.getMessage());
        }
        long endMilliSec = System.currentTimeMillis();
        long seconds = (endMilliSec - startMilliSec) / 1000;
        //System.out.println("All drivers are closed for " + seconds + " seconds.");
    }

    private static Thread closeDriverInParallel(long threadId) {
        Thread thread = new Thread(new Runnable() {
            public void run()
            {
                //System.out.println("Start closing driver for thread: " + threadId);
                WebDriverFactory.closeDriver(threadId);
                //System.out.println("Driver ids closed for thread: " + threadId);
            }});
        thread.start();
        return thread;
    }
}
