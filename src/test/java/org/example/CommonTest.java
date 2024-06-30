package org.example;

import org.example.factory.WebDriverFactory;
import org.example.utils.ServerManager;
import org.testng.annotations.AfterSuite;

public class CommonTest {

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.closeAllDrivers();
        ServerManager.terminateAllSeleniumServers();
        ServerManager.terminateAwsRmiServer();
    }
}
