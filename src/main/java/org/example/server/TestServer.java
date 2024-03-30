package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.Config;
import org.example.data.TestInput;
import org.example.data.TestResult;
import org.example.page.WebFormPage;


import javax.naming.Context;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.TestModes.AWS_LAMBDA;

public class TestServer implements TestServerInterface {
    private static final String LAMBDA_ERROR_MSG_TMP = "AWS Lambda error:\n%s";
    private final Config config = new Config("config.properties");
    static private final ConcurrentMap<Long, Boolean> threadMap = new ConcurrentHashMap<>();

    public TestServer() {
        threadMap.put(Thread.currentThread().threadId(), true);
        System.out.printf("Thread count: %d%n", threadMap.size());
    }

    @Override
    public TestResult signUp(TestInput testInput) {

        if (config.getTestMode().equals(AWS_LAMBDA)) {
            TestServerLambda testServerLambda = new TestServerLambda();
            return testServerLambda.signUp(testInput);
        }

        try {
            WebFormPage webFormPage = new WebFormPage();
            webFormPage.open("https://www.selenium.dev/selenium/web/web-form.html");
            webFormPage.enterIntoTextInput(testInput.textInput());
            webFormPage.enterPassword("Password123");
            webFormPage.enterIntoTextarea(testInput.textareaInput());
            webFormPage.selectDropdownOption(testInput.dropdownSelectedOption());
            webFormPage.selectDataListOption(testInput.dataListSelectOption());
            // TODO: Fix file path for remote run.
            //webFormPage.enterFilePath(testInput.filePath());
            webFormPage.setCheckbox1Value(testInput.checkbox1Value());
            webFormPage.setCheckbox2Value(testInput.checkbox2Value());

            if (testInput.radiobutton1Value()) {
                webFormPage.selectRadiobutton1();
            }

            if (testInput.radiobutton2Value()) {
                webFormPage.selectRadiobutton2();
            }

            webFormPage.pickColor(testInput.color());
            webFormPage.pickDate(testInput.date());
            webFormPage.setRange(testInput.range());

            System.out.println(webFormPage.getURL());

            return new TestResult(
                    webFormPage.getTextInputValue(),
                    webFormPage.getTextareaValue(),
                    webFormPage.getDropdownSelectedOption(),
                    webFormPage.getDataListSelectedOption(),
                    "", //webFormPage.getFilePath(),
                    webFormPage.getCheckbox1Value(),
                    webFormPage.getCheckbox2Value(),
                    webFormPage.getRadiobutton1Value(),
                    webFormPage.getRadiobutton2Value(),
                    webFormPage.getPickedColor(),
                    webFormPage.getPickedDate(),
                    webFormPage.getRange());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
