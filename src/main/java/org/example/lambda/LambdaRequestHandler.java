package org.example.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.LambdaInput;
import org.example.data.SignUpTestInput;
import org.example.server.TestServer;

public class LambdaRequestHandler implements RequestHandler<String, String> {
    private static final String LAMBDA_ERROR_MSG_TMP = "AWS Lambda error:\n%s";

    @Override
    public String handleRequest(String lambdaInputJsonString, Context context) {
        ObjectMapper mapper = new ObjectMapper();
        LambdaInput lambdaInput;
        Object testInput;
        Object testResult;
        String testResultJsonString;

        try {
            lambdaInput = mapper.readValue(lambdaInputJsonString, LambdaInput.class);
            System.out.println("Lambda input object: " + lambdaInput);
        }
        catch (Exception e) {
            System.out.println("Input object mapping error:\n" + e.getMessage());
            return getLambdaError(e.getMessage());
        }

        try {
            testInput = mapper.readValue(lambdaInput.inputData(), SignUpTestInput.class);
            System.out.println("Test input object: " + testInput);
        }
        catch (Exception e) {
            System.out.println("Input object mapping error:\n" + e.getMessage());
            return getLambdaError(e.getMessage());
        }

        try {
            TestServer testServer = new TestServer();
            testResult = testServer.invokeMethod(
                    lambdaInput.methodName(), lambdaInput.paramClassName(), testInput);
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
