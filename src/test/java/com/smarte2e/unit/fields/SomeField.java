package com.smarte2e.unit.fields;

import com.smarte2e.utils.ClassUtils;

public class SomeField {
    private static final String CLASS_PACKAGE_NAME = "com.smarte2e.unit.supplemental.classes";
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