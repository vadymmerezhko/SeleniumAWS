package org.example.unit;

import org.example.enums.Platform;
import org.example.utils.SystemUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class SystemUtilsTest {
    @Test
    public void testIsWindowsOrLinux() {
        Assert.assertNotEquals(SystemUtils.isWindows(), SystemUtils.isLinux());
    }

    @Test
    public void testGetPlatform() {
        Platform platform = SystemUtils.getPlatform();

        switch (platform) {
            case WINDOWS -> Assert.assertTrue(SystemUtils.isWindows());
            case LINUX -> Assert.assertTrue(SystemUtils.isLinux());
            default -> {
                Assert.assertFalse(SystemUtils.isWindows());
                Assert.assertFalse(SystemUtils.isLinux());
            }
        }
    }
    @Test
    public void testRunCommandLine() {
        String command = "echo Hello"; // Simple command that works on both Windows and Unix
        String output = SystemUtils.runCommandLine(command);
        Assert.assertTrue(output.contains("Hello"));
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testRunCommandLineNullInvalid() {
        SystemUtils.runCommandLine(null);
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void testRunCommandLineBlankInvalid() {
        SystemUtils.runCommandLine("   ");
    }
}
