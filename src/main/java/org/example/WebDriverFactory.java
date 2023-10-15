package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebDriverFactory {

    static ConcurrentMap<Long, WebDriver> driverMap = new ConcurrentHashMap<>();

    public static WebDriver getDriver() {

        long threadId = Thread.currentThread().getId();

        if (!driverMap.containsKey(threadId)) {

            /*String ec2InstanceId =
                    AwsManager.runEC2("ImageName", "KeyName", "GroupName");

            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            String ec2InstanceIp = AwsManager.getEC2PublicIp(ec2InstanceId);
            */

            System.out.println("Thread id: " + threadId);
            System.setProperty("webdriver.chrome.driver", "c:\\Selenium\\chromedriver.exe");
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--headless");
            options.addArguments("--disable-gpu");
            //WebDriver driver = new ChromeDriver(options);
            WebDriver driver;
            try {
                //driver = new RemoteWebDriver(new URL("http://" + ec2InstanceIp), options);
                driver = new RemoteWebDriver(new URL("http://54.153.118.158:4444"), options);
                System.out.println("Session created");
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
            driver.manage().window().maximize();
            driverMap.put(threadId, driver);
            return driver;
        } else {
            return driverMap.get(threadId);
        }
    }

    public static void closeDriver() {
        long threadId = Thread.currentThread().getId();
        if (driverMap.containsKey(threadId)) {
            driverMap.get(threadId).quit();
            driverMap.remove(threadId);
        }
    }

    public static void closeAllDrivers() {
        System.out.println("Close all browsers");
        driverMap.values().forEach(WebDriver::quit);
        driverMap.clear();
        System.out.println("All browsers are closed");
    }
}
