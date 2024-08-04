package org.example.servers;

import org.example.data.*;

/**
 * Test server interface.
 */
public interface TestServerInterface {

    /**
     * Fills the Web Form method interface.
     * @param input The input data.
     * @return The output data.
     */
    WebFormPageOutput fillWebForm(WebFormPageInput input);

    TargetPageOutput submitWebForm();
}
