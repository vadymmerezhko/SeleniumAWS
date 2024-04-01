package org.example.server;

import org.example.data.SignUpTestInput;
import org.example.data.SignUpTestResult;

public interface TestServerInterface {
    SignUpTestResult signUp(SignUpTestInput testInput);
}
