package org.example.server;

import org.example.TestResult;
import org.example.page.WebFormPage;
import org.openqa.selenium.*;

public class TestServer {

    WebDriver driver;

    public TestServer(WebDriver driver) {
        this.driver = driver;
    }

    public TestResult signUp(String value) {
        try {

            String multilineText = """
                    Some multiline
                    text
                    here
                    """;

            WebFormPage webFormPage = new WebFormPage();
            webFormPage.open("https://www.selenium.dev/selenium/web/web-form.html");
            webFormPage.enterIntoTextInput(value);
            webFormPage.enterPassword("Password123");
            webFormPage.enterIntoTextarea(multilineText);

            System.out.println(webFormPage.getURL());

            TestResult testResult = new TestResult(
                    webFormPage.getTextInputValue(),
                    webFormPage.getTextareaValue());

            return  testResult;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
