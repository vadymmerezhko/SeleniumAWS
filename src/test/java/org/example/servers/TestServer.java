package org.example.servers;

import lombok.extern.slf4j.Slf4j;
import org.example.data.FillWebFormTestInput;
import org.example.data.FillWebFormTestResult;
import org.example.data.SubmitWebFormTestResult;
import org.example.exceptions.SmartRuntimeException;
import org.example.pages.TargetPage;
import org.example.pages.WebFormPage;
import org.example.tests.BaseTestServer;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Test server implementation class.
 */
@Slf4j
public class TestServer extends BaseTestServer implements TestServerInterface {
    static private final ConcurrentMap<Long, Boolean> threadMap = new ConcurrentHashMap<>();

    /**
     * Test server implementation constructor.
     */
    TestServer() {
        threadMap.put(Thread.currentThread().threadId(), true);
        log.info("Thread count: {}", threadMap.size());
    }

    /**
     * Fill Web Form test method implementation.
     * @param testInput The test input.
     * @return The test result.
     */
    @Override
    public FillWebFormTestResult fillWebForm(FillWebFormTestInput testInput) {

        try {
            WebFormPage webFormPage = new WebFormPage();
            webFormPage.open();
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

            //log.info("Page URL: {}", webFormPage.getURL());

            return new FillWebFormTestResult(
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
            throw new SmartRuntimeException("Sign up filed.", e);
        }
    }

    /**
     * Submit Web Form test method implementation.
     * @return The test result.
     */
    @Override
    public SubmitWebFormTestResult submitWebForm() {
        WebFormPage webFormPage = new WebFormPage();
        webFormPage.submit();

        TargetPage targetPage = new TargetPage();
        String header = targetPage.getHeaderText();
        String status = targetPage.getStatusText();

        return new SubmitWebFormTestResult(header, status);
    }
}
