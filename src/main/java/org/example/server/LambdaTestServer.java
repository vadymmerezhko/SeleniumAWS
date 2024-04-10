package org.example.server;

import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;

class LambdaTestServer extends BaseTestServer implements TestServerInterface {
    @Override
    public SignUpTestResult signUp(SignUpTestInput testInput) {
        return (SignUpTestResult) invokeLambdaFunction("signUp", testInput, SignUpTestResult.class);
    }
}
