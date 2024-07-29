package org.example.servers;

import org.example.configs.TestConfig;

/**
 * Test server manager class.
 */
public class TestServerManager {
    private static final TestConfig config = TestConfig.getInstance();
    private TestServerManager() {}

    /**
     * Returns test server instance.
     * Test server specific is defined in the Config file.
     * @return Test server instance.
     */
    public static synchronized TestServerInterface getTestServer() {
        return switch (config.getTestMode()) {
            case AWS_LAMBDA -> new LambdaTestServer();
            case AWS_RMI -> new RmiTestServer();
            default -> new TestServer();
        };
    }
}
