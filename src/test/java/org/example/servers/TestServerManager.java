package org.example.servers;

/**
 * Test server manager class.
 */
public class TestServerManager {
    private TestServerManager() {}

    /**
     * Returns test server instance.
     * Test server specific is defined in the Config file.
     * @return Test server instance.
     */
    public static synchronized TestServerInterface getTestServer() {
        return new TestService();
    }
}
