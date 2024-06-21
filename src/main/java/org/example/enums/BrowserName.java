package org.example.enums;

public enum BrowserName {

    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge"),
    CHROMIUM("chromium"),
    SAFARI("safari"),
    WEBKIT("webkit");

    private final String name;

    BrowserName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static BrowserName fromString(String name) {
        for (BrowserName value : BrowserName.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new RuntimeException(String.format(
                "Cannot convert '%s' to BrowserName enum item.", name));
    }
}
