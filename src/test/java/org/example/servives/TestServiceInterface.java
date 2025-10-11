package org.example.servives;

import org.example.data.*;

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
