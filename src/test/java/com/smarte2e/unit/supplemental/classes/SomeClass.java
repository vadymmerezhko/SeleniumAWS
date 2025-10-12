package com.smarte2e.unit.supplemental.classes;

import lombok.Getter;
import com.smarte2e.unit.fields.SomeField;

@Getter
public class SomeClass {
    // Two SomeField members to check that right one will be found by ClassUtils.getFieldInstanceName() method
    private final SomeField someField = new SomeField();
    private final SomeField someOtherField = new SomeField();
}
