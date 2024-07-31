package org.example.servers;

import org.example.data.FillWebFormTestInput;
import org.example.data.FillWebFormTestResult;
import org.example.data.SubmitWebFormTestResult;

/**
 * Test server interface.
 */
public interface TestServerInterface {

    /**
     * Fills the Web Form method interface.
     * @param testInput The input data.
     * @return The JSON string output.
     */
    FillWebFormTestResult fillWebForm(FillWebFormTestInput testInput);

    SubmitWebFormTestResult submitWebForm();
}
