package org.example;

import org.example.balancer.LoadBalancer;
import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;
import org.example.server.TestServer;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Path;
import java.nio.file.Paths;

public class BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod() {
        LoadBalancer.getInstance().incrementServerThreadCount();
    }

    @AfterMethod
    public void afterMethod() {
        LoadBalancer.getInstance().decrementServerThreadCount();
    }

    protected void signUp() {
        Path currentRelativePath = Paths.get("pom.xml");
        String currentFolderPath = currentRelativePath.toAbsolutePath().toString();

        SignUpTestInput testInput = new SignUpTestInput(
                "Selenium",
                "Selenium WebDriver",
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

        signUp(testInput);
    }

    private void signUp(SignUpTestInput testInput) {
        TestServer testServer = new TestServer();
        SignUpTestResult testResult = testServer.signUp(testInput);

        Assert.assertEquals(testResult.textInput(), testInput.textInput());
        Assert.assertEquals(testResult.textareaInput(), testInput.textareaInput());
        Assert.assertEquals(testResult.dropdownSelectedOption(), testInput.dropdownSelectedOption());
        Assert.assertEquals(testResult.dataListSelectOption(), testInput.dataListSelectOption());
        // TODO: Fix file path for remote run.
        //Assert.assertTrue((testResult.filePath().contains("pom.xml")));
        //TODO: fix checkbox value for Android
        Assert.assertEquals(testResult.checkbox1Value(), testInput.checkbox1Value());
        Assert.assertEquals(testResult.radiobutton1Value(), testInput.radiobutton1Value());
        Assert.assertEquals(testResult.radiobutton2Value(), testInput.radiobutton2Value());
        Assert.assertEquals(testResult.color(), testInput.color());
        Assert.assertEquals(testResult.date(), testInput.date());
        Assert.assertEquals(testResult.range(), testInput.range());

        System.out.println("Test passed for Thread: " + Thread.currentThread().threadId());
    }
}
