package org.example.server;

import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;

class RmiTestServer extends BaseTestServer implements TestServerInterface{
    @Override
    public SignUpTestResult signUp(SignUpTestInput testInput) {
        return (SignUpTestResult) invokeRemoteMethod("signUp", testInput, SignUpTestResult.class);
    }
}
