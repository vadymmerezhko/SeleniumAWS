package org.example.data;

import lombok.extern.slf4j.Slf4j;
import org.example.enums.BrowserName;
import org.example.enums.TestMode;
import org.example.exceptions.SmartRuntimeException;
import org.example.utils.DataValidationUtils;

import static org.example.constants.Settings.CONFIG_PROPERTIES_FILE_PATH;

/**
 * The configuration file class.
 * See README.md fi;e for more details.
 */
@Slf4j
public class Config extends BaseConfig {
    private static final String TESTNG_FILE = "testngFile";
    private static final String THREAD_COUNT = "threadCount";
    private static final String TEST_MODE = "testMode";
    private static final String BROWSER = "browser";
    private static final String HEADLESS = "headless";
    private static final String REMOTE_HOST = "remoteHost";
    private static final String EMULATORS = "emulators";
    private static final String ACCESS_KEY = "accessKey";
    private static final String SECRET_KEY = "secretKey";
    private static final String START_DATE = "startDate";
    private static final String SCREENSHOT_ON_FAIL = "screenshotOnFail";
    private static final String VIDEO_ON_FAIL = "videoOnFail";
    private static final String DEBUG_MODE = "debugMode";
    private static final String HIGHLIGHT = "highlight";
    private static final String STEP_DELAY = "stepDelay";
    private static final String BROWSER_SIZE = "browserSize";
    private static final String PAGES_FOLDER_PATH = "pagesFolderPath";

    public static Config getInstance() {
        return new Config(CONFIG_PROPERTIES_FILE_PATH);
    }

    /**
     * Config class constructor by the config file path.
     * @param filePath The config file path.
     */
    private Config(String filePath) {
        super(filePath);
        log.debug("{} file path: {}", getClass().getSimpleName(), filePath);
        DataValidationUtils.validateFilePath(filePath, "filePath");
    }

    /**
     * Returns the TestNG file name.
     * @return The TestNG file name.
     */
    synchronized public String getTestngFile() {
        return getStringProperty(TESTNG_FILE);
    }

    /**
     * Returns the maximal thread count.
     * @return The maximal tread count.
     */
    synchronized public int getThreadCount() {
        validateDebugModeProperty();
        return getIntegerProperty(THREAD_COUNT);
    }

    /**
     * Returns the test mode name.
     * @return The test mode name.
     */
    synchronized public TestMode getTestMode() {
        return TestMode.fromString(getStringProperty(TEST_MODE));
    }

    /**
     * Returns remote Selenium server URL.
     * @return The remote host URL.
     */
    synchronized public String getRemoteHost() {
        return getStringProperty(REMOTE_HOST);
    }

    /**
     * Returns the AWS access key.
     * @return The AWS access key.
     */
    synchronized public String getAccessKey() {
        return getStringProperty(ACCESS_KEY);
    }

    /**
     * Returns the AWS secret key.
     * @return The AWS secret key.
     */
    synchronized public String getSecretKey() {
        return getStringProperty(SECRET_KEY);
    }

    /**
     * Returns test start date.
     * @return The test start date.
     */
    synchronized public String getStartDate() {
        return getStringProperty(START_DATE);
    }

    /**
     * Returns Appium emulator name by its index.
     * @param index The emulator index.
     * @return The emulator name.
     */
    synchronized public String getEmulator(int index) {
        String emulators = getStringProperty(EMULATORS);
        String[] emulatorsArray = emulators.split(VALUES_DELIMITER);

        if (emulatorsArray.length <= index) {
            throw new SmartRuntimeException(String.format(
                    "Wrong emulator index: %d.", index));
        }

        return emulatorsArray[index];
    }

    /**
     * Returns the browser name and version (optional).
     * @return The browser name and version.
     */
    synchronized public String getBrowser() {
        return getStringProperty(BROWSER);
    }

    /**
     * Returns the browser name.
     * @return The browser name.
     */
    synchronized public BrowserName getBrowserName() {
        String browser = getStringProperty(BROWSER);
        return BrowserName.fromString(getSubValue(browser, 0));
    }

    /**
     * Returns the browser version.
     * @return The browser version.
     */
    synchronized public String getBrowserVersion() {
        String browser = getStringProperty(BROWSER);
        try {
            return getSubValue(browser, 1);
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "The '%s' browser version is undefined.", browser));
        }
    }

    /**
     * Returns true if browser version is defined or false otherwise.
     * @return The browser version.
     */
    synchronized public boolean isBrowserVersionDefined() {
        String browser = getStringProperty(BROWSER);
        return browser.split(VALUES_DELIMITER).length == 2;
    }

    /**
     * Returns the browser size.
     * @return The browser size.
     */
    synchronized public String getBrowserSize() {
        return getStringProperty(BROWSER_SIZE);
    }

    /**
     * Returns the browser width.
     * @return The browser width.
     */
    synchronized public int getBrowseWidth() {
        String browserSize = getBrowserSize();
        try {
            return getIntegerSubValue(browserSize, 0);
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "The browser width is undefined: %s", browserSize));
        }
    }

    /**
     * Returns the browser height.
     * @return The browser height.
     */
    synchronized public int getBrowseHeight() {
        String browserSize = getBrowserSize();
        try {
            return getIntegerSubValue(browserSize, 1);
        } catch (Exception e) {
            throw new SmartRuntimeException(String.format(
                    "The browser height is undefined: %s", browserSize));
        }
    }

    /**
     * Returns true/false headless flag.
     * @return The headless flag.
     */
    synchronized public boolean getHeadless() {
        validateDebugModeProperty();
        return getBooleanProperty(HEADLESS);
    }

    /**
     * Returns true/false take screenshot on fail flag.
     * @return The screenshot on fail flag.
     */
    synchronized public boolean getScreenshotOnFail() {
        return getBooleanProperty(SCREENSHOT_ON_FAIL);
    }

    /**
     * Returns true/false record video on fail flag.
     * @return The video on fail flag.
     */
    synchronized public boolean getVideoOnFail() {
        return getBooleanProperty(VIDEO_ON_FAIL);
    }

    /**
     * Returns true/false debug mode flag.
     * @return The debug mode flag.
     */
    synchronized public boolean getDebugMode() {
        validateDebugModeProperty();
        return getBooleanProperty(DEBUG_MODE);
    }

    /**
     * Returns true/false highlight element flag.
     * @return The headless flag.
     */
    synchronized public boolean getHighlightElement() {
        return getBooleanProperty(HIGHLIGHT);
    }

    /**
     * Returns step delay milliseconds.
     * @return The step delay.
     */
    synchronized public int getStepDelay() {
        return getIntegerProperty(STEP_DELAY);
    }

    /**
     * Returns page objects folder path.
     * @return The AWS secret key.
     */
    synchronized public String getPagesFolderPath() {
        return getStringProperty(PAGES_FOLDER_PATH);
    }

    private void validateDebugModeProperty() {
        boolean debugMode = getBooleanProperty(DEBUG_MODE);

        if (debugMode) {
            String format = "Wrong '%1$s' configuration parameter value '%2$s' for debugMode=true.\n" +
                            "It should be Test %1$s=%3$s (\"-D%1$s=%3$s\").";
            String errorMessage = null;
            String testMode = getStringProperty(TEST_MODE);
            String headless = getStringProperty(HEADLESS);
            String threadCount = getStringProperty(THREAD_COUNT);

            if (!testMode.equals("local")) {
                errorMessage = String.format(format, TEST_MODE, testMode, "local");
            }
            else if (!headless.equals("false")) {
                errorMessage = String.format(format, HEADLESS, headless, "false");
            }
            else if (!threadCount.equals("1")) {
                errorMessage = String.format(format, THREAD_COUNT, threadCount, "1");
            }

            if (errorMessage != null) {
                log.error(errorMessage);
                System.exit(-1);
            }
        }
    }
}
