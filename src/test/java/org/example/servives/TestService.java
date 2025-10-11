package org.example.servives;

import lombok.extern.slf4j.Slf4j;
import org.example.data.*;
import org.example.pages.TargetPage;
import org.example.pages.WebFormPage;
import org.example.utils.DataValidationUtils;

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
        DataValidationUtils.validateNotNull(input, "input");

        WebFormPage webFormPage = new WebFormPage();
        return webFormPage.fillWebForm(input);
    }

    /**
     * Submit Web Form test method implementation.
     * @return The test output.
     */
    @Override
    public TargetPageOutput submitWebForm() {
        WebFormPage webFormPage = new WebFormPage();
        webFormPage.submitWebForm();

        TargetPage targetPage = new TargetPage();
        return  targetPage.getOutputData();
    }
}
