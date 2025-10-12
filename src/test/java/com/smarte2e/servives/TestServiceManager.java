package com.smarte2e.servives;

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
    public static synchronized TestServiceInterface getService() {
        return new TestService();
    }
}
