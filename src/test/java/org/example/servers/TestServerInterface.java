package org.example.servers;

import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;

/**
 * Test server interface.
 */
public interface TestServerInterface {

    /**
     * Sign up method interface with JSON string input and output.
     * @param testInput The JSON string input.
     * @return The JSON string output.
     */
    SignUpTestResult signUp(SignUpTestInput testInput);
}
