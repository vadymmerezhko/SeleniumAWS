package org.example.utils;

import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;
import io.appium.java_client.service.local.flags.GeneralServerFlag;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static io.appium.java_client.service.local.flags.GeneralServerFlag.BASEPATH;

public class AppiumManager {
    private static final String MOBILE_DEVICE_JSON_PATH = "src/test/resources/devices/mobile.json";
    private static final ConcurrentMap<Long, AppiumDriverLocalService> appiumServiceMap = new ConcurrentHashMap<>();

    public static String getDeviceName(String emulatorName) {
        return getStringProperty(emulatorName,"deviceName");
    }

    public static String getPlatformName(String emulatorName) {
        return getStringProperty(emulatorName, "platformName");
    }

    public static String getPlatformVersion(String emulatorName) {
        return getStringProperty(emulatorName,"platformVersion");
    }

    public static String getBrowserName(String emulatorName) {
        return getStringProperty(emulatorName,"browserName");
    }

    public static String getBrowserVersion(String emulatorName) {
        return getStringProperty(emulatorName,"browserVersion");
    }

    public static void startEmulator(String deviceName) {
        CommandLineExecutor.runCommandLine("emulator -avd " + deviceName);
    }

    public static synchronized URL startAppiumServer(int threadCount) {
        long threadId = Thread.currentThread().threadId();

        if (appiumServiceMap.containsKey(threadId)) {
            return appiumServiceMap.get(threadId).getUrl();
        }

        int portNumber = 4723 + (int)threadId % threadCount;

        try {
            AppiumServiceBuilder builder = new AppiumServiceBuilder();
            builder.withIPAddress("127.0.0.1")
                    .usingPort(portNumber)
                    .withAppiumJS(
                            new File("C:\\Users\\vadym\\AppData\\Roaming\\npm\\node_modules\\appium\\build\\lib\\main.js"))
                    .usingDriverExecutable(new File("C:\\Program Files\\nodejs\\node.exe"))
                    .withArgument(BASEPATH, "/wd/hub")
                    //.withArgument(GeneralServerFlag.SESSION_OVERRIDE)
                    .withArgument(GeneralServerFlag.LOG_LEVEL, "debug");

            AppiumDriverLocalService appiumService = AppiumDriverLocalService.buildService(builder);
            appiumService.start();
            appiumServiceMap.put(threadId, appiumService);
            return appiumService.getUrl();
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot start Appium server.\n" + e.getMessage());
        }
    }

    public static void stopAppiumServer() {
        long threadId = Thread.currentThread().threadId();

        if (appiumServiceMap.containsKey(threadId)) {
            appiumServiceMap.get(threadId).stop();        }
    }

    public static void stopAllAppiumServers() {
        appiumServiceMap.values().forEach(AppiumDriverLocalService::stop);
    }

    public static void stopAllEmulators() {
        String killCommandLine =
                SystemManager.isWindows() ? "taskkill /f /t /im qemu-system-x86_64.exe" : "pkill qemu-system-x86_64";
        CommandLineExecutor.runCommandLine(killCommandLine);
    }

    private static String getStringProperty(String emulatorName, String propertyName) {
        try {
            JSONObject mobileDevices = new JSONObject(FileManager.readFile(MOBILE_DEVICE_JSON_PATH));
            JSONObject emulator = mobileDevices.getJSONObject(emulatorName);
            return emulator.getString(propertyName);
        }
        catch (Exception e) {
            throw new RuntimeException(
                    String.format("Cannot get mobile device property: %s\n%s", propertyName, e.getMessage()));
        }
    }
}