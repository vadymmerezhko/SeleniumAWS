package org.example.server;

import org.example.data.Config;
import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;
import org.example.page.WebFormPage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.CONFIG_PROPERTIES_FILE_NAME;
import static org.example.constants.TestModes.AWS_LAMBDA;
import static org.example.constants.TestModes.AWS_RMI;

public class TestServer extends BaseTestServer implements TestServerInterface {
    private final Config config = new Config(CONFIG_PROPERTIES_FILE_NAME);
    static private final ConcurrentMap<Long, Boolean> threadMap = new ConcurrentHashMap<>();

    public TestServer() {
        threadMap.put(Thread.currentThread().threadId(), true);
        System.out.printf("Thread count: %d%n", threadMap.size());
    }

    @Override
    public SignUpTestResult signUp(SignUpTestInput testInput) {
        String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        String testMode = config.getTestMode();

        if (testMode.equals(AWS_LAMBDA)) {
            return (SignUpTestResult) invokeLambdaFunction(methodName, testInput, SignUpTestResult.class);
        }
        else if (testMode.equals(AWS_RMI)) {
            return (SignUpTestResult) invokeRemoteMethod(methodName, testInput, SignUpTestResult.class);
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
