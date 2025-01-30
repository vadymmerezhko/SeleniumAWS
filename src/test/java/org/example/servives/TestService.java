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
            int thisYear = LocalDate.now().getYear();
            String descriptionKeyword = "my-textarea";

            webFormPage.open();
            log.info("Web Form page URL: {}", webFormPage.getCurrentUrl());

            webFormPage.getPassword().enterText("Password123");
            webFormPage.getDescription().setKeyword(descriptionKeyword);
            input.getDeliveryDate().setKeyword(thisYear);
            webFormPage.setAllInputs(input);

            WebFormPageOutput output = new WebFormPageOutput();
            output.getDeliveryDate().setKeyword(thisYear);
            webFormPage.setAllOutputs(output);

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
        Button submitButton =  webFormPage.getSubmit();
        submitButton.click();

        TargetPage targetPage = new TargetPage();
        TargetPageOutput output = new TargetPageOutput();
        targetPage.setAllOutputs(output);

        log.debug("Target page output data is returned: {}", output);
        return output;
    }
}
