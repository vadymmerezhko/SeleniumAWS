package org.example.server;

import org.example.data.Config;
import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;
import org.example.page.WebFormPage;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.TestModes.AWS_LAMBDA;

public class TestServer implements TestServerInterface {
    private final Config config = new Config("config.properties");
    static private final ConcurrentMap<Long, Boolean> threadMap = new ConcurrentHashMap<>();

    public TestServer() {
        threadMap.put(Thread.currentThread().threadId(), true);
        System.out.printf("Thread count: %d%n", threadMap.size());
    }

    public Object invokeMethod(String methodName, String paramClassName, Object param) {

        try {
            Class<?> paramClass = Class.forName(paramClassName);
            Method method = this.getClass().getMethod(methodName, paramClass);
            return method.invoke(this, param);
        }
        catch (ClassNotFoundException | IllegalAccessException |
               InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SignUpTestResult signUp(SignUpTestInput testInput) {

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

            return new SignUpTestResult(
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
