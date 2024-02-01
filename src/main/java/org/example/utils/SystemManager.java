package org.example.utils;

import org.apache.maven.surefire.shared.lang3.SystemUtils;
import org.example.constants.DataModel;
import org.example.constants.Platforms;

public class SystemManager {
    private SystemManager() {}

    public static boolean isWindows() {
        return SystemUtils.IS_OS_WINDOWS;
    }

    public static String getPlatform() {

        if (SystemUtils.IS_OS_WINDOWS) {
            return Platforms.WINDOWS;
        }
        else if (SystemUtils.IS_OS_LINUX) {
            return Platforms.LINUX;
        }
        else if (SystemUtils.IS_OS_MAC) {
            return Platforms.MAC;
        }
        else {
            throw new RuntimeException("This OS is not supported: " + System.getProperty("os.name"));
        }
    }

    public static String getDataModel() {
        String dataModel = System.getProperty("sun.arch.data.model");

        switch (dataModel) {
            case "32" -> {
                return DataModel.BIT32;
            }
            case "64" -> {
                return DataModel.BIT64;
            }
            default -> throw new RuntimeException("Unsupported data model: " + dataModel);
        }
    }
}
