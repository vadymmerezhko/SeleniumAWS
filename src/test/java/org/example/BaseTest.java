package org.example;

import org.example.data.TestInput;
import org.example.data.TestResult;
import org.example.driver.WebDriverFactory;
import org.example.server.TestServer;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {


    protected WebDriver driver;

    @AfterTest
    public void afterTest() {
        //WebDriverFactory.closeDriver();
    }

    protected void signUp() {
        driver = WebDriverFactory.getDriver();

        Path currentRelativePath = Paths.get("pom.xml");
        String currentFolderPath = currentRelativePath.toAbsolutePath().toString();

        TestInput testInput = new TestInput(
                "Selenium",
                "Selenium\nWebDriver",
                "Two",
                "Chicago",
                currentFolderPath,
                false,
                true,
                false,
                true,
                "#0088ff",
                "05/23/1970",
                2);

        TestServer testServer = new TestServer(driver);
        TestResult testResult = testServer.signUp(testInput);
        Assert.assertEquals(testResult.textInput(), testInput.textInput());
        Assert.assertEquals(testResult.textareaInput(), testInput.textareaInput());
        Assert.assertEquals(testResult.dropdownSelectedOption(), testInput.dropdownSelectedOption());
        Assert.assertEquals(testResult.dataListSelectOption(), testInput.dataListSelectOption());
        // TODO: Fix file path for remote run.
        //Assert.assertTrue((testResult.filePath().contains("pom.xml")));
        Assert.assertEquals(testResult.checkbox1Value(), testInput.checkbox1Value());
        Assert.assertEquals(testResult.checkbox2Value(), testInput.checkbox2Value());
        Assert.assertEquals(testResult.color(), testInput.color());
        Assert.assertEquals(testResult.date(), testInput.date());
        Assert.assertEquals(testResult.range(), testInput.range());
    }
}
