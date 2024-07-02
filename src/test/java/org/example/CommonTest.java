package org.example;

import org.example.factories.WebDriverFactory;
import org.example.utils.ServerUtils;
import org.testng.annotations.AfterSuite;

public class CommonTest {

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.closeAllDrivers();
        ServerUtils.terminateAllSeleniumServers();
        ServerUtils.terminateAwsRmiServer();
    }
}
