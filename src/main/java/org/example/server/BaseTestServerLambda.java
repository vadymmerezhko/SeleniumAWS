package org.example.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.data.LambdaInput;
import org.example.driver.WebDriverFactory;
import org.example.utils.AwsManager;

import static org.example.constants.Settings.AWS_LAMBDA_FUNCTION_ARN;

public abstract class BaseTestServerLambda {

    protected String invokeLambdaFunction(LambdaInput lambdaInput) {
        ObjectMapper mapper = new ObjectMapper();
        String lambdaInputJsonString;

        try {
            lambdaInputJsonString = mapper.writeValueAsString(lambdaInput);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String lambdaOutputJsonString = AwsManager.invokeLambdaFunction(
                AWS_LAMBDA_FUNCTION_ARN,
                lambdaInputJsonString);

        WebDriverFactory.closeDriver();

        return lambdaOutputJsonString;
    }
}
