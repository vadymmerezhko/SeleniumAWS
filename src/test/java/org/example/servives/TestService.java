package org.example.servives;

import lombok.extern.slf4j.Slf4j;
import org.example.data.*;
import org.example.ui.elements.Button;
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
public class TestService implements TestServiceInterface {
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
            int thisYear = LocalDate.now().getYear();
            String textAreaKeyword = "my-textarea";

            webFormPage.open();
            webFormPage.getTextInput().enterText(input.getTextInput());
            webFormPage.getPassword().enterText("Password123");
            webFormPage.getTextarea().setKeyword(textAreaKeyword);
            webFormPage.getTextarea().enterText(input.getTextareaInput());
            webFormPage.getDropdown().selectOption(input.getDropdownSelectedOption());
            webFormPage.getDataList().selectOption(input.getDataListSelectOption());
            webFormPage.getCheckbox1().setValue(input.getCheckbox1Value());
            webFormPage.getCheckbox2().setValue(input.getCheckbox2Value());
            webFormPage.getFileInput().enterFilePath(input.getFilePath());

            if (input.getRadiobutton1Value().toBoolean()) {
                webFormPage.getRadiobutton1().select();
            }
            if (input.getRadiobutton2Value().toBoolean()) {
                webFormPage.getRadiobutton2().select();
            }
            webFormPage.getColorPicker().pickColor(input.getColor());
            input.getDate().setKeyword(thisYear);
            webFormPage.getDatePicker().pickDate(input.getDate());
            webFormPage.getRangeSlider().setValue(input.getRange());
            log.info("Page URL: {}", webFormPage.getCurrentUrl());

            output.getTextInput().setValue(webFormPage.getTextInput().getValue());
            output.getTextareaInput().setValue(webFormPage.getTextarea().getValue());
            output.getDropdownSelectedOption().setValue(webFormPage.getDropdown().getSelectedOption());
            output.getDataListSelectOption().setValue(webFormPage.getDataList().getValue());
            output.getCheckbox1Value().setValue(webFormPage.getCheckbox1().getValue());
            output.getCheckbox2Value().setValue(webFormPage.getCheckbox2().getValue());
            output.getRadiobutton1Value().setValue(webFormPage.getRadiobutton1().isSelected());
            output.getRadiobutton2Value().setValue(webFormPage.getRadiobutton2().isSelected());
            output.getColor().setValue(webFormPage.getColorPicker().getValue());
            output.getDate().setKeyword(thisYear);
            output.getDate().setValue(webFormPage.getDatePicker().getValue());
            output.getRange().setValue(webFormPage.getRangeSlider().getValue());
            output.getFilePath().setValue(webFormPage.getFileInput().getValue());
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
        Button submitButton =  webFormPage.getSubmitButton();
        submitButton.click();

        TargetPage targetPage = new TargetPage();
        TargetPageOutput output = new TargetPageOutput();
        output.getHeader().setValue(targetPage.getHeader().getText());
        output.getStatus().setValue(targetPage.getStatus().getText());
        log.debug("Target page output data is returned: {}", output);
        return output;
    }
}
