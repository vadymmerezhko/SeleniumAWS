package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.MethodInput;
import org.example.driver.WebDriverFactory;
import org.example.utils.AwsManager;

import static org.example.constants.Settings.AWS_LAMBDA_FUNCTION_ARN;

public abstract class BaseTestServerLambda extends BaseTestServer {
    protected String invokeLambdaFunction(MethodInput methodInput) {
        String lambdaInputJsonString = methodInputToSting(methodInput);
        String methodOutput = AwsManager.invokeLambdaFunction(
                AWS_LAMBDA_FUNCTION_ARN,
                lambdaInputJsonString);

        if (methodOutput.startsWith("AWS Lambda error")) {
            throw new RuntimeException(methodOutput);
        }
        return methodOutput;
    }
}
