package org.example.enums;

public enum DataModel {
    BIT32("32"),
    BIT64("64");

    private final String name;

    DataModel(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static DataModel fromString(String name) {
        for (DataModel value : DataModel.values()) {
            if (value.name.equalsIgnoreCase(name)) {
                return value;
            }
        }
        throw new RuntimeException(String.format(
                "Cannot convert '%s' to DataModel enum item.", name));
    }
}
