package org.example.server;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.MethodInput;

public class TestServerRequestHandler implements RequestHandler<String, String> {
    private static final String REQUEST_HANDLER_ERROR_MSG_TMP = "Request handler error:\n%s";

    @Override
    public String handleRequest(String methodInputJsonString, Context context) {
        ObjectMapper mapper = new ObjectMapper();
        MethodInput methodInput;
        Object testResult;
        String testResultJsonString;

        try {
            methodInput = mapper.readValue(methodInputJsonString, MethodInput.class);
            System.out.println("Method input object: " + methodInput);
        }
        catch (Exception e) {
            System.out.println("Method input object mapping error:\n" + e.getMessage());
            return getErrorMessage(e.getMessage());
        }

        try {
            TestServer testServer = new TestServer();
            testResult = testServer.invokeMethod(
                    methodInput.methodName(),
                    methodInput.paramClassName(),
                    methodInput.inputData());
        }
        catch (Exception e) {
            System.out.println("Test server method invocation error:\n" + e.getMessage());
            return getErrorMessage(e.getMessage());
        }

        try {
            testResultJsonString = mapper.writeValueAsString(testResult);
            System.out.println("Test output string:" + testResultJsonString);
        }
        catch (Exception e) {
            return getErrorMessage(e.getMessage());
        }

        return testResultJsonString;
    }

    private String getErrorMessage(String errorDescription) {
        return String.format(REQUEST_HANDLER_ERROR_MSG_TMP, errorDescription);
    }
}
