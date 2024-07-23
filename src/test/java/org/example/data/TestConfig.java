package org.example.data;


import org.example.enums.TestMode;

import static org.example.constants.TestSettings.TEST_CONFIG_FILE_PATH;

/**
 * The test configuration file class.
 * See README.md file for more details.
 */
public class TestConfig extends BaseConfig{
    private static final String DEBUG_FAIL = "debugFail";

    public static TestConfig getInstance() {
        return new TestConfig(TEST_CONFIG_FILE_PATH);
    }

    /**
     * TestConfig class constructor by the config file path.
     *
     * @param filePath The config file path.
     */
    public TestConfig(String filePath) {
        super(filePath);
    }

    /**
     * Returns the test mode name.
     * @return The test mode name.
     */
    synchronized public TestMode getTestMode() {
        return Config.getInstance().getTestMode();
    }

    /**
     * Returns true/false debug fail flag.
     * @return The debug fail flag.
     */
    synchronized public boolean getDebugFail() {
        return getBooleanProperty(DEBUG_FAIL);
    }
}
