package org.example.configs;


import org.example.data.BaseConfig;
import org.example.enums.TestMode;

import static org.example.constants.Settings.TEST_CONFIG_FILE_PATH;

/**
 * The test configuration file class.
 * See README.md file for more details.
 */
public class TestConfig extends BaseConfig {
    private static final String DEBUG_FAIL = "debugFail";
    private static final String SITE_HOST = "siteHost";

    /**
     * Creates test config instance.
     * @return The test config instance.
     */
    public static TestConfig getInstance() {
        return new TestConfig(TEST_CONFIG_FILE_PATH);
    }

    /**
     * Creates test config instance from the config file.
     * @param configFilePath The config file path.
     * @return The test config instance.
     */
    public static TestConfig getInstance(String configFilePath) {
        return new TestConfig(configFilePath);
    }

    /**
     * TestConfig class constructor by the config file path.
     *
     * @param filePath The config file path.
     */
    private TestConfig(String filePath) {
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

    /**
     * Returns AUT site host in format like: "https://<host>:<port>"
     * @return The site host.
     */
    synchronized public String getSiteHost() {
        return getStringProperty(SITE_HOST);
    }
}
