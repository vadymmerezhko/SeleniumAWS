package org.example;

import org.example.constants.Settings;
import org.example.driver.WebDriverFactory;
import org.example.rmi.RmiClient;
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
