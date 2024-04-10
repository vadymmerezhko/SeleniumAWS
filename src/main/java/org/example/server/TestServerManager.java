package org.example.server;

import org.example.data.Config;

import java.util.concurrent.atomic.AtomicReference;

import static org.example.constants.Settings.CONFIG_PROPERTIES_FILE_NAME;
import static org.example.constants.TestModes.AWS_LAMBDA;
import static org.example.constants.TestModes.AWS_RMI;

public class TestServerManager {
    private static final AtomicReference<Config> config =
            new AtomicReference<>(new Config(CONFIG_PROPERTIES_FILE_NAME));
    private TestServerManager() {}

    public static TestServerInterface getTestServer() {
        return switch (config.get().getTestMode()) {
            case AWS_LAMBDA -> new LambdaTestServer();
            case AWS_RMI -> new RmiTestServer();
            default -> new TestServer();
        };
    }
}
