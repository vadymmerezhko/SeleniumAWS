package com.smarte2e.servives;

import com.smarte2e.data.LoginPageInput;
import com.smarte2e.data.LoginPageOutput;
import com.smarte2e.data.ProductsPageHeaderOutput;
import com.smarte2e.data.TargetPageOutput;
import com.smarte2e.data.WebFormPageInput;
import com.smarte2e.data.WebFormPageOutput;

/**
 * Test server interface.
 */
public interface TestServiceInterface {

    /**
     * Fills the Web Form method interface.
     * @param input The input data.
     * @return The output data.
     */
    WebFormPageOutput fillWebForm(WebFormPageInput input);

    /**
     * Submits the Web Form
     * @return The Target page output data.
     */
    TargetPageOutput submitWebForm();

    /**
     * Fills login page form with input data.
     * @param input Login form input data.
     * @return Login form output data.
     */
    LoginPageOutput fillLoginPage(LoginPageInput input);

    /**
     * Submits Login page form.
     * @return The Product page header output data.
     */
    ProductsPageHeaderOutput submitLoginPage();
}
