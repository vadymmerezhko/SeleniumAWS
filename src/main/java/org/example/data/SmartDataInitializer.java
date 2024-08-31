package org.example.data;

import java.lang.reflect.Field;

public class SmartDataInitializer {

    public static void initialize(SmartData parent) {
        // Check if the class is annotated with @SmartPage
        if (parent.getClass().isAnnotationPresent(org.example.annotations.SmartValue.class)) {
            // Get all declared fields in the class
            Field[] fields = parent.getClass().getDeclaredFields();

            for (Field field : fields) {
                // Check if the field is of type SmartValue
                if (SmartValue.class.isAssignableFrom(field.getType())) {
                    try {
                        // Make the private field accessible
                        field.setAccessible(true);

                        // Create a new instance of SmartValue
                        Class<?> fieldType = field.getType();
                        Object value = fieldType.getDeclaredConstructor().newInstance();
                        ((SmartValue)value).setParent(parent);

                        // Assign the new SmartValue instance to the field
                        field.set(parent, value);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                        throw new RuntimeException("Failed to initialize SmartElement: " + field.getName(), e);
                    }
                }
            }
        }
    }
}
