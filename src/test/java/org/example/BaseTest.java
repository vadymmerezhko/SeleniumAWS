package org.example;

import org.example.driver.WebDriverFactory;
import org.example.server.TestServer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class BaseTest {

    protected WebDriver driver;

    protected void signUp() {
        driver = WebDriverFactory.getDriver();
        String inputValue = "Selenium";
        TestServer testServer = new TestServer(driver);
        String actualValue = testServer.signUp(inputValue);
        Assert.assertEquals(actualValue, inputValue);
    }
}
