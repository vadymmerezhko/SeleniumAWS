package org.example.server;

import org.example.data.LambdaInput;
import org.example.data.LambdaOutput;
import org.example.utils.AwsManager;

import static org.example.constants.Settings.AWS_LAMBDA_FUNCTION_ARN;

public abstract class BaseTestServerLambda {

    protected LambdaOutput invokeLambdaFunction(LambdaInput lambdaInput) {

        String testOutputJsonString = AwsManager.invokeLambdaFunction(
                AWS_LAMBDA_FUNCTION_ARN,
                lambdaInput.inputData());

        return new LambdaOutput(
                lambdaInput.threadId(),
                lambdaInput.methodName(),
                testOutputJsonString);
    }
}
