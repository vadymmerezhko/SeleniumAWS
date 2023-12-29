package org.example.server;

import org.openqa.selenium.*;

import java.io.File;
import java.util.List;
import java.util.Set;

public class TestServer {

    WebDriver driver;

    public TestServer(WebDriver driver) {
        this.driver = driver;
    }

    public String signUp(String value) {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.id("my-text-id"));
        textBox.sendKeys(value);
        System.out.println(driver.getCurrentUrl());

        return textBox.getAttribute("value");
    }
}
