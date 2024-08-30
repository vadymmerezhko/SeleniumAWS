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

    TargetPageOutput submitWebForm();
}
