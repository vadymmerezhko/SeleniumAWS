package org.example.unit;

import org.example.configs.TestConfig;
import org.example.enums.TestMode;
import org.example.exceptions.SmartRuntimeException;
import org.testng.annotations.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import static org.testng.Assert.*;

public class TestConfigTest {

    private TestConfig testConfig;

    @BeforeClass
    public void setUp() {
        // Mock the configuration properties
        Properties properties = new Properties();
        properties.setProperty("debugFail", "true");
        properties.setProperty("siteHost", "https://localhost:8080");

        // Write properties to a temp file
        try {
            Path tempFile = java.nio.file.Files.createTempFile("testConfig", ".properties");
            FileWriter writer = new java.io.FileWriter(tempFile.toFile());
            properties.store(writer, "Test Configuration");
            writer.close();

            // Initialize TestConfig with the temp file
            testConfig = TestConfig.getInstance(tempFile.toString());
        } catch (Exception e) {
            throw new RuntimeException("Failed to set up test configuration.", e);
        }
    }

    @Test
    public void testGetTestMode() {
        // Assume that Config.getInstance() is properly set up
        assertEquals(testConfig.getTestMode(), TestMode.LOCAL_AUTO);
    }

    @Test
    public void testGetDebugFail() {
        assertTrue(testConfig.getDebugFail());
    }

    @Test
    public void testGetSiteHost() {
        assertEquals(testConfig.getSiteHost(), "https://localhost:8080");
    }
    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetDebugFailInvalidProperty() {
        // Set up a config with an invalid property value
        Properties properties = new Properties();
        properties.setProperty("debugFail", "invalid_boolean");
        Path tempFile;

        try {
            tempFile = java.nio.file.Files.createTempFile("invalidTestConfig", ".properties");
            FileWriter writer = new java.io.FileWriter(tempFile.toFile());
            properties.store(writer, "Invalid Test Configuration");
            writer.close();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Failed to create temporary config file.", e);
        }
        TestConfig invalidTestConfig = TestConfig.getInstance(tempFile.toString());
        invalidTestConfig.getDebugFail();
    }

    @Test(expectedExceptions = SmartRuntimeException.class)
    public void testGetSiteHostInvalidProperty() {
        Path tempFile;
        try {
            // Set up a config with a missing property
            tempFile = java.nio.file.Files.createTempFile("missingPropertyTestConfig", ".properties");
            FileWriter writer = new java.io.FileWriter(tempFile.toFile());
            new Properties().store(writer, "Missing Property Test Configuration");
            writer.close();
            }
        catch (Exception e) {
            System.out.println(e.getMessage());
            throw new RuntimeException("Failed to set up missing property test configuration.", e);
       }
        TestConfig missingPropertyTestConfig = TestConfig.getInstance(tempFile.toString());
        missingPropertyTestConfig.getSiteHost();
    }
}
