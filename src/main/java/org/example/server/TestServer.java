package org.example.server;

import org.example.data.Config;
import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;
import org.example.page.WebFormPage;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.example.constants.Settings.CONFIG_PROPERTIES_FILE_NAME;

public class TestServer extends BaseTestServer implements TestServerInterface {
    static private final ConcurrentMap<Long, Boolean> threadMap = new ConcurrentHashMap<>();

    TestServer() {
        threadMap.put(Thread.currentThread().threadId(), true);
        System.out.printf("Thread count: %d%n", threadMap.size());
    }

    @Override
    public SignUpTestResult signUp(SignUpTestInput testInput) {

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
