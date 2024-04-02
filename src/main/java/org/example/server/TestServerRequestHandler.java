package org.example.server;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.MethodInput;
import org.example.data.SignUpTestInput;

public class TestServerRequestHandler implements RequestHandler<String, String> {
    private static final String LAMBDA_ERROR_MSG_TMP = "AWS Lambda error:\n%s";

    @Override
    public String handleRequest(String lambdaInputJsonString, Context context) {
        ObjectMapper mapper = new ObjectMapper();
        MethodInput methodInput;
        Object testInput;
        Object testResult;
        String testResultJsonString;

        try {
            methodInput = mapper.readValue(lambdaInputJsonString, MethodInput.class);
            System.out.println("Lambda input object: " + methodInput);
        }
        catch (Exception e) {
            System.out.println("Input object mapping error:\n" + e.getMessage());
            return getLambdaError(e.getMessage());
        }

        try {
            testInput = mapper.readValue(methodInput.inputData(), SignUpTestInput.class);
            System.out.println("Test input object: " + testInput);
        }
        catch (Exception e) {
            System.out.println("Input object mapping error:\n" + e.getMessage());
            return getLambdaError(e.getMessage());
        }

        try {
            TestServer testServer = new TestServer();
            testResult = testServer.invokeMethod(
                    methodInput.methodName(), methodInput.paramClassName(), testInput);
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
