package com.smarte2e.enums;

import com.smarte2e.exceptions.SmartRuntimeException;

public enum TestMode {
    LOCAL("local"),
    LOCAL_AUTO("local_auto"),
    LOCAL_DOCKER("local_docker"),
    LOCAL_DOCKER_AUTO("local_docker_auto"),
    LOCAL_PLAYWRIGHT("local_playwright"),
    LOCAL_ACCESSIBILITY("local_accessibility"),
    AWS_DOCKER("aws_docker"),
    AWS_DEVICE_FARM("aws_device_farm"),
    REMOTE("remote"),
    LOCAL_APPIUM("local_appium");
    private final String name;

    TestMode(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static TestMode fromString(String name) {
        for (TestMode value : TestMode.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new SmartRuntimeException(String.format(
                "Cannot convert '%s' to TestMode enum item.", name));
    }
}
