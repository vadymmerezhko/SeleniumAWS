package org.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

public class SeleniumTest8 {

    WebDriver driver;

    @BeforeClass
    public void testSetup()
    {
    }

    @BeforeMethod
    public void openBrowser()
    {
        driver = WebDriverFactory.getDriver();
    }

    @Test(description="This method validates the sign up functionality", invocationCount = 8)
    public void signUp()
    {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));
        WebElement submitButton = driver.findElement(By.cssSelector("button"));

        textBox.sendKeys("Selenium");
        submitButton.click();

        WebElement message = driver.findElement(By.id("message"));
        Assert.assertEquals(message.getText(), "Received!");
    }

    @AfterMethod
    public void postSignUp()
    {
        System.out.println(driver.getCurrentUrl());
        //driver.quit();
    }

    @AfterClass
    public void afterClass()
    {
        //driver.quit();
    }
}
