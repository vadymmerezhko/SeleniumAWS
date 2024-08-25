package org.example.servers;

import lombok.extern.slf4j.Slf4j;
import org.example.data.*;
import org.example.exceptions.SmartRuntimeException;
import org.example.pages.TargetPage;
import org.example.pages.WebFormPage;

import java.time.LocalDate;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Test server implementation class.
 */
@Slf4j
public class TestService implements TestServerInterface {
    static private final ConcurrentMap<Long, Boolean> threadMap = new ConcurrentHashMap<>();

    /**
     * Test server implementation constructor.
     */
    TestService() {
        threadMap.put(Thread.currentThread().threadId(), true);
        log.info("Thread count: {}", threadMap.size());
    }

    /**
     * Fill Web Form test method implementation.
     * @param input The test input.
     * @return The test output.
     */
    @Override
    public WebFormPageOutput fillWebForm(WebFormPageInput input) {
        try {
            WebFormPage webFormPage = new WebFormPage();
            WebFormPageOutput output = new WebFormPageOutput();

            webFormPage.open();
            webFormPage.enterIntoTextInput(input.getTextInput());
            webFormPage.enterPassword("Password123");
            input.getTextareaInput().setKeyword(textAreaKeyword);
            webFormPage.enterIntoTextarea(input.getTextareaInput());
            webFormPage.selectDropdownOption(input.getDropdownSelectedOption());
            webFormPage.selectDataListOption(input.getDataListSelectOption());
            // TODO: Fix file path for remote run.
            //webFormPage.enterFilePath(input.getFilePath());
            webFormPage.setCheckbox1Value(input.getCheckbox1Value());
            webFormPage.setCheckbox2Value(input.getCheckbox2Value());

            if (input.getRadiobutton1Value().toBoolean()) {
                webFormPage.selectRadiobutton1();
            }
            if (input.getRadiobutton2Value().toBoolean()) {
                webFormPage.selectRadiobutton2();
            }
            webFormPage.pickColor(input.getColor());
            input.getDate().setKeyword(thisYear);
            webFormPage.pickDate(input.getDate());
            webFormPage.setRange(input.getRange());
            log.info("Page URL: {}", webFormPage.getCurrentUrl());

            output.getTextareaInput().setValue(webFormPage.getTextInputValue());
            output.getTextareaInput().setKeyword(textAreaKeyword);
            output.getTextareaInput().setValue(webFormPage.getTextareaValue());
            output.getDropdownSelectedOption().setValue(webFormPage.getDropdownSelectedOption());
            output.getDataListSelectOption().setValue(webFormPage.getDataListSelectedOption());
            // TODO fix file path on remote driver.
            //output.getFilePath().setString(webFormPage.getFilePath())
            output.getCheckbox1Value().setValue(webFormPage.getCheckbox1Value());
            output.getCheckbox2Value().setValue(webFormPage.getCheckbox2Value());
            output.getRadiobutton1Value().setValue(webFormPage.getRadiobutton1Value());
            output.getRadiobutton2Value().setValue(webFormPage.getRadiobutton2Value());
            output.getColor().setValue(webFormPage.getColor());
            output.getDate().setKeyword(thisYear);
            output.getDate().setValue(webFormPage.getDate());
            output.getRange().setValue(webFormPage.getRange());
            log.debug("Web Form page output data is returned: {}", output);
            return output;
        }
        catch (Exception e) {
            throw new SmartRuntimeException("Filling the Web Form failed.", e);
        }
    }

    /**
     * Submit Web Form test method implementation.
     * @return The test output.
     */
    @Override
    public TargetPageOutput submitWebForm() {
        WebFormPage webFormPage = new WebFormPage();
        webFormPage.submit();

        TargetPage targetPage = new TargetPage();
        TargetPageOutput output = new TargetPageOutput();
        output.getHeader().setValue(targetPage.getHeaderText());
        output.getStatus().setValue(targetPage.getStatusText());
        log.debug("Target page output data is returned: {}", output);
        return output;
    }
}
