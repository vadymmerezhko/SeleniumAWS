package org.example.servives;

/**
 * Test server manager class.
 */
public class TestServiceManager {
    private TestServiceManager() {}

    /**
     * Returns test server instance.
     * Test server specific is defined in the Config file.
     * @return Test server instance.
     */
    public static synchronized TestServiceInterface getTestServer() {
        return new TestService();
    }
}
