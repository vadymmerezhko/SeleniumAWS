package org.example.utils;

import org.example.enums.DataModel;
import org.example.enums.Platform;

import static org.example.enums.DataModel.BIT32;
import static org.example.enums.DataModel.BIT64;
import static org.example.enums.Platform.*;

/**
 * System manager class.
 */
public final class SystemUtils {

    private SystemUtils() {}

    /**
     * Returns true for Windows platform, or false otherwise.
     * @return true/false flag.
     */
    public static boolean isWindows() {
        return org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_WINDOWS;
    }

    /**
     * Returns true for Linux platform, or false otherwise.
     * @return true/false flag.
     */
    public static boolean isLinux() {
        return org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_LINUX;
    }

    /**
     * Returns platform name.
     * @return The platform name..
     */
    public static Platform getPlatform() {

        if (org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS;
        }
        else if (org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_LINUX) {
            return LINUX;
        }
        else if (org.apache.maven.surefire.shared.lang3.SystemUtils.IS_OS_MAC) {
            return MAC;
        }
        else {
            throw new RuntimeException("This OS is not supported: " + System.getProperty("os.name"));
        }
    }

    /**
     * Returns CPU data model.
     * @return The data model.
     */
    public static DataModel getDataModel() {
        String dataModel = System.getProperty("sun.arch.data.model");

        switch (dataModel) {
            case "32" -> {
                return BIT32;
            }
            case "64" -> {
                return BIT64;
            }
            default -> throw new RuntimeException("Unsupported data model: " + dataModel);
        }
    }
}
