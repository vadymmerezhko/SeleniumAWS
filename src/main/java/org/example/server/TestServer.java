package org.example.server;

import org.example.driver.by.SmartBy;
import org.openqa.selenium.*;

public class TestServer {

    WebDriver driver;

    public TestServer(WebDriver driver) {
        this.driver = driver;
    }

    public String signUp(String value) {
        try {
            driver.get("https://www.selenium.dev/selenium/web/web-form.html");
            //WebElement textBox = driver.findElement(SmartBy.id("my-text-id"));
            WebElement textBox = driver.findElement(SmartBy.inputLabelTextContains("Text input"));
            textBox.sendKeys(value);
            System.out.println(driver.getCurrentUrl());

            return textBox.getAttribute("value");
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
