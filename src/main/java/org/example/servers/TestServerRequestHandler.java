package org.example.servers;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.data.MethodInput;
import org.example.utils.RecordUtils;

import static org.example.constants.Settings.REQUEST_HANDLER_ERROR_MSG;

/**
 * Test server request handler class.
 */
@Slf4j
public class TestServerRequestHandler implements RequestHandler<String, String> {
    private static final String REQUEST_HANDLER_ERROR_MSG_TMP = REQUEST_HANDLER_ERROR_MSG + ":\n%s";

    /**
     * Handles RMI request with JSON input string and context parameters.
     * @param methodInputJsonString The method JSON string input.
     * @param context The context.
     * @return The output JSON string.
     */
    @Override
    public String handleRequest(String methodInputJsonString, Context context) {
        try {
            log.debug("Method input Json:\n{}", methodInputJsonString);

            MethodInput methodInput =
                    (MethodInput) RecordUtils.stringToRecord(methodInputJsonString, MethodInput.class);

            log.debug("Method input object:\n{}", methodInput.toString());

            TestServer testServer = new TestServer();
            Object testResult = testServer.invokeMethod(
                    methodInput.methodName(),
                    methodInput.paramClassName(),
                    methodInput.inputData());

            log.debug("Method output object:\n{}", testResult.toString());
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
