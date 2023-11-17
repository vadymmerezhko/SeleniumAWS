package org.example;

import org.example.driver.WebDriverFactory;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class CommonTest {

    @BeforeSuite
    public  void beforeSuite() {
        try {
/*            ServerManager.createServerInstances(
                    Settings.SELENIUM_SERVERS_COUNT,
                    Settings.AWS_IMAGE_ID,
                    Settings.SECURITY_KEY_PAIR_NAME,
                    Settings.SECURITY_GROUP_NAME,
                    Settings.USER_DATA);*/
        } catch (Exception e) {
            System.out.println("Cannot create all servers:\n" + e.getMessage());
            System.exit(-1);
        }
    }

    @AfterSuite()
    public void afterSuite() {
        WebDriverFactory.closeAllDrivers();
        //ServerManager.terminateAllSeleniumServers();
    }
}
