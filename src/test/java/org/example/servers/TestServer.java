package org.example.servers;

import lombok.extern.slf4j.Slf4j;
import org.example.data.*;
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
     * @param input The test input.
     * @return The test output.
     */
    @Override
    public WebFormPageOutput fillWebForm(WebFormPageInput input) {

        try {
            WebFormPage webFormPage = new WebFormPage();
            WebFormPageOutput output = new WebFormPageOutput();
            output.initialize();

            webFormPage.open();
            webFormPage.enterIntoTextInput(input.getTextInput());
            webFormPage.enterPassword("Password123");
            webFormPage.enterIntoTextarea(input.getTextareaInput());
            webFormPage.selectDropdownOption(input.getDropdownSelectedOption());
            webFormPage.selectDataListOption(input.getDataListSelectOption());
            // TODO: Fix file path for remote run.
            //webFormPage.enterFilePath(input.getFilePath());
            webFormPage.setCheckbox1Value(input.getCheckbox1Value());
            webFormPage.setCheckbox2Value(input.getCheckbox2Value());

            if (input.getRadiobutton1Value()) {
                webFormPage.selectRadiobutton1();
            }
            if (input.getRadiobutton2Value()) {
                webFormPage.selectRadiobutton2();
            }
            webFormPage.pickColor(input.getColor());
            webFormPage.pickDate(input.getDate());
            webFormPage.setRange(input.getRange());
            log.info("Page URL: {}", webFormPage.getCurrentUrl());

            output.setTexInput(webFormPage.getTextInputValue())
            .setTextareaInput(webFormPage.getTextareaValue())
            .setDropdownSelectedOption(webFormPage.getDropdownSelectedOption())
            .setDataListSelectOption(webFormPage.getDataListSelectedOption())
            // TODO fix file path on remote driver.
            //.setFilePath(webFormPage.getFilePath())
            .setCheckbox1Value(webFormPage.getCheckbox1Value())
            .setCheckbox2Value(webFormPage.getCheckbox2Value())
            .setRadiobutton1Value(webFormPage.getRadiobutton1Value())
            .setRadiobutton2Value(webFormPage.getRadiobutton2Value())
            .setColor(webFormPage.getColor())
            .setDate(webFormPage.getDate())
            .setRange(webFormPage.getRange());
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
        output.initialize()
                .setHeader(targetPage.getHeaderText())
                .setStatus(targetPage.getStatusText());
        log.debug("Target page output data is returned: {}", output);
        return output;
    }
}
