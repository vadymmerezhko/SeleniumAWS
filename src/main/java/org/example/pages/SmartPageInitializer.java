package org.example.pages;

import java.lang.reflect.Field;

public class SmartPageInitializer {

    public static void initialize(SmartPage page) {
        // Check if the class is annotated with @SmartPage
        if (page.getClass().isAnnotationPresent(org.example.annotations.SmartElement.class)) {
            // Get all declared fields in the class
            Field[] fields = page.getClass().getDeclaredFields();

            for (Field field : fields) {
                // Check if the field is of type SmartElement
                if (SmartElement.class.isAssignableFrom(field.getType())) {
                    try {
                        // Make the private field accessible
                        field.setAccessible(true);

                        // Create a new instance of SmartElement
                        Class<?> fieldType = field.getType();
                        Object element = fieldType.getDeclaredConstructor().newInstance();
                        ((SmartElement)element).setPage(page);

                        // Assign the new SmartElement instance to the field
                        field.set(page, element);
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
