package org.example;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class CommonTest {

    @BeforeSuite
    public  void beforeSuite() {
        try {
            WebDriverFactory.createServerInstances();
        } catch (Exception e) {
            System.out.println("Cannot create all servers:\n" + e.getMessage());
            Assert.fail(e.getMessage());
        }
    }

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.closeAllDrivers();
        WebDriverFactory.terminateAllSeleniumServers();
    }
}
