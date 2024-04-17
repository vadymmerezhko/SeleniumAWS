package org.example.server;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.example.data.MethodInput;
import org.example.utils.RecordUtils;

import static org.example.constants.Settings.REQUEST_HANDLER_ERROR_MSG;

public class TestServerRequestHandler implements RequestHandler<String, String> {
    private static final String REQUEST_HANDLER_ERROR_MSG_TMP = REQUEST_HANDLER_ERROR_MSG + ":\n%s";

    @Override
    public String handleRequest(String methodInputJsonString, Context context) {
        try {
            System.out.println("Method input Json:\n" + methodInputJsonString);

            MethodInput methodInput =
                    (MethodInput) RecordUtils.stringToRecord(methodInputJsonString, MethodInput.class);

            System.out.println("Method input object:\n" + methodInput.toString());

            TestServer testServer = new TestServer();
            Object testResult = testServer.invokeMethod(
                    methodInput.methodName(),
                    methodInput.paramClassName(),
                    methodInput.inputData());

            System.out.println("Method output object:\n" + testResult.toString());
            return RecordUtils.recordToString(testResult);
        }
        catch (Exception e) {
            return getErrorMessage(getErrorMessage(e.getMessage()));
        }
    }

    private String getErrorMessage(String errorDescription) {
        return String.format(REQUEST_HANDLER_ERROR_MSG_TMP, errorDescription);
    }
}
