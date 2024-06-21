package org.example.enums;

public enum Platform {
    WINDOWS("windows"),
    LINUX("linux"),
    MAC("mac");

    private final String name;

    Platform(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Platform fromString(String name) {
        for (Platform value : Platform.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new RuntimeException(String.format(
                "Cannot convert '%s' to Platform enum item.", name));
    }
}
