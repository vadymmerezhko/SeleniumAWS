package com.smarte2e.servives;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.data.LoginPageInput;
import com.smarte2e.data.LoginPageOutput;
import com.smarte2e.data.ProductsPageHeaderOutput;
import com.smarte2e.data.TargetPageOutput;
import com.smarte2e.data.WebFormPageInput;
import com.smarte2e.data.WebFormPageOutput;
import com.smarte2e.pages.LoginPage;
import com.smarte2e.pages.ProductsPage;
import com.smarte2e.pages.TargetPage;
import com.smarte2e.pages.WebFormPage;
import com.smarte2e.utils.DataValidator;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Test server implementation class.
 */
@Slf4j
public class TestService implements com.smarte2e.servives.TestServiceInterface {
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
        DataValidator.notNull(input, "input");

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

    /**
     * Fills login page form with input data.
     * @param input Login form input data.
     * @return Login form output data.
     */
    @Override
    public LoginPageOutput fillLoginPage(LoginPageInput input) {
        LoginPage loginPage = new LoginPage();
        return loginPage.fillLoginForm(input);
    }

    /**
     * Submits Login page form.
     * @return The Product page header output data.
     */
    @Override
    public ProductsPageHeaderOutput submitLoginPage() {
        LoginPage loginPage = new LoginPage();
        loginPage.submitLoginForm();

        ProductsPage productsPage = new ProductsPage();
        return productsPage.getHeaderOutputData();
    }
}
