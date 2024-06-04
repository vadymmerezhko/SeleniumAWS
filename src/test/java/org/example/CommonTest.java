package org.example;

import org.example.factory.WebDriverFactory;
import org.example.utils.ServerManager;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class CommonTest {

    @BeforeSuite
    public  void beforeSuite() {
    }

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.closeAllDrivers();
        ServerManager.terminateAllSeleniumServers();
        ServerManager.terminateAwsRmiServer();
    }
}
