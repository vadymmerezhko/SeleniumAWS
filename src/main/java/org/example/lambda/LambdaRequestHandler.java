package org.example.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.TestInput;
import org.example.data.TestResult;
import org.example.server.TestServer;

public class LambdaRequestHandler implements RequestHandler<String, String> {
    private static final String LAMBDA_ERROR_MSG_TMP = "AWS Lambda error:\n%s";

    @Override
    public String handleRequest(String input, Context context) {
        ObjectMapper mapper = new ObjectMapper();
        TestInput testInput;
        TestResult testResult;
        String testResultJsonString;

        System.out.println("Test input: " + input);

        try {
            testInput = mapper.readValue(input, TestInput.class);
            System.out.println("Test input object: " + testInput);
        }
        catch (Exception e) {
            System.out.println("Input object mapping error:\n" + e.getMessage());
            return getLambdaError(e.getMessage());
        }

        try {
            TestServer testServer = new TestServer();
            testResult = testServer.signUp(testInput);
            System.out.println("Test result object: " + testResult);
        }
        catch (Exception e) {
            System.out.println("Sign-up error:\n" + e.getMessage());
            return getLambdaError(e.getMessage());
        }

        try {
            testResultJsonString = mapper.writeValueAsString(testResult);
            System.out.println("Test output string:" + testResultJsonString);
        }
        catch (Exception e) {
            return getLambdaError(e.getMessage());
        }

        return testResultJsonString;
    }

    private String getLambdaError(String errorDescription) {
        return String.format(LAMBDA_ERROR_MSG_TMP, errorDescription);
    }
}
