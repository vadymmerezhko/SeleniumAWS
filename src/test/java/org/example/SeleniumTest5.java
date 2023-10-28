package org.example;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.annotations.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class SeleniumTest5 {

    WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void openBrowser() {
        driver = WebDriverFactory.getDriver();
    }

    @AfterMethod(alwaysRun = true)
    public void decrementThreadPool() {
        LoadBalancer.getInstance().decrementServerThreadCount();
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16)
    public void signUp1() {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys("Selenium");
        Assert.assertTrue(textBox.isEnabled());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16)
    public void signUp2() {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys("Selenium");
        Assert.assertTrue(textBox.isEnabled());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16)
    public void signUp3() {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys("Selenium");
        Assert.assertTrue(textBox.isEnabled());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16)
    public void signUp4() {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys("Selenium");
        Assert.assertTrue(textBox.isEnabled());
        System.out.println(driver.getCurrentUrl());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16)
    public void signUp5() {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys("Selenium");
        Assert.assertTrue(textBox.isEnabled());
        System.out.println(driver.getCurrentUrl());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16)
    public void signUp6() {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys("Selenium");
        Assert.assertTrue(textBox.isEnabled());
        System.out.println(driver.getCurrentUrl());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16)
    public void signUp7() {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys("Selenium");
        Assert.assertTrue(textBox.isEnabled());
        System.out.println(driver.getCurrentUrl());
    }

    @Test(description = "This method validates the sign up functionality", invocationCount = 16)
    public void signUp8() {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys("Selenium");
        Assert.assertTrue(textBox.isEnabled());
        System.out.println(driver.getCurrentUrl());
    }
}
