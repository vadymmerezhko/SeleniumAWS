package org.example.enums;

public enum TestMode {
    LOCAL("local"),
    LOCAL_AUTO("local_auto"),
    LOCAL_DOCKER("local_docker"),
    LOCAL_PLAYWRIGHT("local_playwright"),
    LOCAL_ACCESSIBILITY("local_accessibility"),
    AWS_DOCKER("aws_docker"),
    AWS_DEVICE_FARM("aws_device_farm"),
    REMOTE("remote"),
    LOCAL_APPIUM("local_appium"),
    AWS_LAMBDA("aws_lambda"),
    AWS_RMI("aws_rmi");
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
        throw new RuntimeException(String.format(
                "Cannot convert '%s' to TestMode enum item.", name));
    }
}
