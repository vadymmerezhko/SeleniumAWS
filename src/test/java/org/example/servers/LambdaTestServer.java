package org.example.servers;

import org.example.data.FillWebFormTestInput;
import org.example.data.FillWebFormTestResult;
import org.example.data.SubmitWebFormTestResult;
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
    public FillWebFormTestResult fillWebForm(FillWebFormTestInput testInput) {
        return (FillWebFormTestResult) invokeLambdaFunction("fillWebForm", testInput, FillWebFormTestResult.class);
    }

    @Override
    public SubmitWebFormTestResult submitWebForm() {
        return null;
    }
}
