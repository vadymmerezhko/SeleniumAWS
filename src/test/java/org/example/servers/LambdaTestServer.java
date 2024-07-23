package org.example.servers;

import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;
import org.example.tests.BaseTestServer;

/**
 * AWS Lambda test server implementation class.
 */
public class LambdaTestServer extends BaseTestServer implements TestServerInterface {

    /**
     * Sign up method implementation with JSON string input and output.
     * @param testInput The JSON string input.
     * @return The JSON string output.
     */
    @Override
    public SignUpTestResult signUp(SignUpTestInput testInput) {
        return (SignUpTestResult) invokeLambdaFunction("signUp", testInput, SignUpTestResult.class);
    }
}
