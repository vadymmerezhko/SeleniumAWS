package com.smarte2e.unit;

import com.smarte2e.configs.Config;
import com.smarte2e.enums.BrowserName;
import com.smarte2e.enums.TestMode;
import com.smarte2e.exceptions.SmartRuntimeException;
import org.testng.annotations.*;

import java.util.Properties;

import static org.testng.Assert.*;

public class ConfigTest {

    private Config config;

    @BeforeClass
    public void setUp() {
        // Mock the configuration properties
        Properties properties = new Properties();
        properties.setProperty("testngFile", "testng.xml");
        properties.setProperty("threadCount", "2");
        properties.setProperty("testMode", "local");
        properties.setProperty("browser", "chrome");
        properties.setProperty("headless", "false");
        properties.setProperty("remoteHost", "http://localhost:4444");
        properties.setProperty("accessKey", "dummyAccessKey");
        properties.setProperty("secretKey", "dummySecretKey");
        properties.setProperty("startDate", "2022-01-01");
        properties.setProperty("retryWait", "1000");
        properties.setProperty("retryTimeout", "60");
        properties.setProperty("emulators", "emulator1:emulator2");
        properties.setProperty("screenshotOnFail", "true");
        properties.setProperty("videoOnFail", "false");
        properties.setProperty("debugMode", "false");
        properties.setProperty("highlight", "true");
        properties.setProperty("stepDelay", "500");
        properties.setProperty("browserSize", "1920:1080");
        properties.setProperty("pagesFolderPath", "src/test/pages");
        properties.setProperty("dataFolderPath", "src/test/data");
        properties.setProperty("imagesFolderPath", "src/test/images");
        properties.setProperty("retainBrowser", "true");
        properties.setProperty("pageWaitTimeout", "30");
        properties.setProperty("elementWaitTimeout", "10");
        properties.setProperty("elementWaitDelay", "100");
        properties.setProperty("colorsThreshold", "10%");
        properties.setProperty("pointsThreshold", "5%");

        // Write properties to a temp file
        try {
            java.nio.file.Path tempFile = java.nio.file.Files.createTempFile("config", ".properties");
            java.io.FileWriter writer = new java.io.FileWriter(tempFile.toFile());
            properties.store(writer, "Test Configuration");
            writer.close();

            // Initialize Config with the temp file
            config = Config.getInstance(tempFile.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test configuration.", e);
        }
    }

    @Test
    public void testGetTestngFile() {
        assertEquals(config.getTestngFile(), "testng.xml");
    }

    @Test
    public void testGetThreadCount() {
        assertEquals(config.getThreadCount(), 2);
    }

    @Test
    public void testGetTestMode() {
        assertEquals(config.getTestMode(), TestMode.LOCAL);
    }

    @Test
    public void testGetRemoteHost() {
        assertEquals(config.getRemoteHost(), "http://localhost:4444");
    }

    @Test
    public void testGetAccessKey() {
        assertEquals(config.getAccessKey(), "dummyAccessKey");
    }

    @Test
    public void testGetSecretKey() {
        assertEquals(config.getSecretKey(), "dummySecretKey");
    }

    @Test
    public void testGetStartDate() {
        assertEquals(config.getStartDate(), "2022-01-01");
    }

    @Test
    public void testGetRetryWaitMSec() {
        assertEquals(config.getRetryWaitMSec(), 1000);
    }

    @Test
    public void testGetRetryTimeoutSec() {
        assertEquals(config.getRetryTimeoutSec(), 60);
    }

    @Test
    public void testGetEmulator() {
        assertEquals(config.getEmulator(0), "emulator1");
        assertEquals(config.getEmulator(1), "emulator2");
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetEmulatorInvalidIndex() {
        config.getEmulator(2);
    }

    @Test
    public void testGetBrowser() {
        assertEquals(config.getBrowser(), "chrome");
    }

    @Test
    public void testGetBrowserName() {
        assertEquals(config.getBrowserName(), BrowserName.CHROME);
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetBrowserVersionUndefined() {
        config.getBrowserVersion();
    }

    @Test
    public void testIsBrowserVersionDefined() {
        assertFalse(config.isBrowserVersionDefined());
    }

    @Test
    public void testGetBrowserSize() {
        assertEquals(config.getBrowserSize(), "1920:1080");
    }

    @Test
    public void testGetBrowseWidth() {
        assertEquals(config.getBrowseWidth(), 1920);
    }

    @Test
    public void testGetBrowseHeight() {
        assertEquals(config.getBrowseHeight(), 1080);
    }

    @Test
    public void testGetHeadless() {
        assertFalse(config.getHeadless());
    }

    @Test
    public void testGetScreenshotOnFail() {
        assertTrue(config.getScreenshotOnFail());
    }

    @Test
    public void testGetVideoOnFail() {
        assertFalse(config.getVideoOnFail());
    }

    @Test
    public void testGetDebugMode() {
        assertFalse(config.getDebugMode());
    }

    @Test
    public void testGetHighlightElement() {
        assertTrue(config.getHighlightElement());
    }

    @Test
    public void testGetStepDelay() {
        assertEquals(config.getStepDelay(), 500);
    }

    @Test
    public void testGetPagesFolderPath() {
        assertEquals(config.getPagesFolderPath(), "src/test/pages");
    }

    @Test
    public void testGetDataFolderPath() {
        assertEquals(config.getDataFolderPath(), "src/test/data");
    }

    @Test
    public void testGetImagesFolderPath() {
        assertEquals(config.getImagesFolderPath(), "src/test/images");
    }

    @Test
    public void testGetRetainBrowser() {
        assertTrue(config.getRetainBrowser());
    }

    @Test
    public void testGetPageWaitTimeout() {
        assertEquals(config.getPageWaitTimeout(), 30);
    }

    @Test
    public void testGetElementWaitTimeout() {
        assertEquals(config.getElementWaitTimeout(), 10);
    }

    @Test
    public void testGetElementWaitDelay() {
        assertEquals(config.getElementWaitDelay(), 100);
    }

    @Test
    public void testGetColorsThreshold() {
        assertEquals(config.getColorsThreshold(), 10);
    }

    @Test
    public void testGetPixelsThreshold() {
        assertEquals(config.getPixelsThreshold(), 5);
    }
}