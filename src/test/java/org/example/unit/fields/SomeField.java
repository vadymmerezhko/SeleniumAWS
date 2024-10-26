package org.example.unit.fields;

import org.example.utils.ClassUtils;

public class SomeField {
    private static final String CLASS_PACKAGE_NAME = "org.example.unit.supplemental.classes";
    private final String declaringClassName;

    public SomeField() {
        declaringClassName = ClassUtils.getDeclaringClassName(CLASS_PACKAGE_NAME);
    }

    public String getDeclaringClassName() {
        return declaringClassName;
    }

    public String getFieldName() {
        return ClassUtils.getFieldInstanceName(declaringClassName, this);
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object instanceof SomeField actualSomeField) {
            return declaringClassName.equals(actualSomeField.declaringClassName);
        }
        return false;
    }
}