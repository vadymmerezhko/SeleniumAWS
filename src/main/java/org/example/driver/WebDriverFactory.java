package org.example.driver;

import org.example.LoadBalancer;
import org.example.TimeOut;
import org.example.TimeOutException;
import org.example.Waiter;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebDriverFactory {
    static private final ConcurrentMap<Long, WebDriver> driverMap = new ConcurrentHashMap<>();
    static private final int WAIT_SELENIUM_GRID_TIMEOUT = 30;
    private static final LoadBalancer loadBalancer = LoadBalancer.getInstance();

    public static WebDriver getDriver() {
        WebDriver driver;
        long threadId = Thread.currentThread().getId();
/*        String ec2InstanceIp;
        loadBalancer.incrementServerThreadCount();
        long serverId = loadBalancer.getThreadServerId();
        System.out.println("Serer Id: " + serverId);*/

        if (!driverMap.containsKey(threadId)) {
            System.out.println("Thread: " + threadId);
/*            ec2InstanceIp = loadBalancer.getServerPublicIp(serverId);

            try {
                waitForSeleniumGrid(ec2InstanceIp);
            }
            catch (TimeOutException e) {
                System.out.println("Wait Selenium Grid timeout!");
                loadBalancer.lockSever(serverId);
                return getDriver();
            }*/

            //driver = new RobustWebDriver(getRemoteWebDriver(ec2InstanceIp));
            driver = new RobustWebDriver(getLocalWebDriver());
            driverMap.put(threadId, driver);
        }
        else {
            driver = driverMap.get(threadId);
            driver.manage().deleteAllCookies();
        }
        return driver;
    }

    public static WebDriver getLocalWebDriver() {
        //System.setProperty("webdriver.chrome.driver", "c:\\Selenium\\chromedriver.exe");
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // headless only
        options.addArguments("--disable-gpu"); // applicable to Windows os only
        options.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
        options.addArguments("--no-sandbox"); // bypass OS security model
        options.addArguments("--disable-extensions"); // disabling extensions
        options.addArguments("disable-infobars"); // disabling infobars
        return new ChromeDriver(options);
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
                System.out.println("Session created");
                return driver;
            } catch (Exception e) {
                exception = e;
                repeatCount--;
                Waiter.waitSeconds(1);
                System.out.println("Repeat getting WebDriver");
            }
        }
        System.out.println(exception.getMessage());
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
                System.out.println("Waiting for: " + ec2InstanceIp);
                Waiter.waitSeconds(5);
            }
        }
        finally {
            tempDriver.quit();
        }
    }

    public static void closeDriver() {
        long threadId = Thread.currentThread().getId();
        closeDriver(threadId);
    }

    private static void closeDriver(long threadId) {
        if (driverMap.containsKey(threadId)) {
            driverMap.get(threadId).quit();
            driverMap.remove(threadId);
        }
    }

    public static void closeAllDrivers() {
        driverMap.keySet().forEach(WebDriverFactory::closeDriver);
        System.out.println("All browsers are closed");
    }
}
