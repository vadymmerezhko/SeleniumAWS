package org.example.server;

import org.example.data.TestInput;
import org.example.data.TestResult;

public interface TestServerInterface {
    TestResult signUp(TestInput testInput);
}
