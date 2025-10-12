package com.smarte2e.utils;

import lombok.extern.slf4j.Slf4j;
import com.smarte2e.enums.DataModel;
import com.smarte2e.enums.Platform;
import com.smarte2e.exceptions.SmartRuntimeException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.smarte2e.enums.DataModel.BIT32;
import static com.smarte2e.enums.DataModel.BIT64;
import static com.smarte2e.enums.Platform.*;

/**
 * System manager class.
 */
@Slf4j
public final class SystemUtils {

    private SystemUtils() {}

    /**
     * Returns true for Windows platform, or false otherwise.
     * @return true/false flag.
     */
    public static boolean isWindows() {
        boolean result =  org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_WINDOWS;
        log.debug("Is Windows: {}", result);
        return result;
    }

    /**
     * Returns true for Linux platform, or false otherwise.
     * @return true/false flag.
     */
    public static boolean isLinux() {
        boolean result = org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_LINUX;
        log.debug("Is Linux: {}", result);
        return result;
    }

    /**
     * Returns platform name.
     * @return The platform name..
     */
    public static Platform getPlatform() {
        Platform platform;

        if (org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_WINDOWS) {
            platform = WINDOWS;
        }
        else if (org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_LINUX) {
            platform = LINUX;
        }
        else if (org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_MAC) {
            platform = MAC;
        }
        else {
            throw new SmartRuntimeException(String.format(
                    "This OS is not supported: %s.", System.getProperty("os.name")));
        }
        log.debug("The platform: {}.", platform);
        return platform;
    }

    /**
     * Returns CPU data model.
     * @return The data model.
     */
    public static DataModel getDataModel() {
        String dataModel = System.getProperty("sun.arch.data.model");
        DataModel result;

        switch (dataModel) {
            case "32" -> result = BIT32;
            case "64" -> result = BIT64;
            default -> throw new SmartRuntimeException(String.format(
                    "Unsupported data model: %s.", dataModel));
        }
        log.debug("Data model: {}.", result);
        return result;
    }

    /**
     * Runs command line and returns output string.
     * @param command The command line.
     * @return The output string.
     */
    public static String runCommandLine(String command) {
        DataValidator.notBlank(command, "command");

        StringBuilder output = new StringBuilder();

        if (SystemUtils.isWindows()) {
            command = "cmd.exe /c" + command;
        }
        Runtime runtime = Runtime.getRuntime();
        Process process;
        log.debug("Running command line: {}", command);

        try {
            process = runtime.exec(command);
            BufferedReader stdInput = new BufferedReader(new
                    InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(process.getErrorStream()));
            // Read the output from the command
            String s;
            while ((s = stdInput.readLine()) != null) {
                output.append(s).append('\n');
                log.debug(s);
            }
            // Read any errors from the attempted command
            while ((s = stdError.readLine()) != null) {
                output.append(s).append('\n');
                log.debug(s);
            }
        } catch (IOException e) {
            throw new SmartRuntimeException(String.format(
                    "Cannot run command line: %s", command), e);
        }
        return output.toString();
    }
}
