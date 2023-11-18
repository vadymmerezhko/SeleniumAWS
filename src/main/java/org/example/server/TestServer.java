package org.example.server;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class TestServer {

    WebDriver driver;

    public TestServer(WebDriver driver) {
        this.driver = driver;
    }

    public String signUp(String value) {
        driver.get("https://www.selenium.dev/selenium/web/web-form.html");
        WebElement textBox = driver.findElement(By.name("my-text"));

        textBox.sendKeys(value);
        System.out.println(driver.getCurrentUrl());

        return textBox.getAttribute("value");
    }
}
