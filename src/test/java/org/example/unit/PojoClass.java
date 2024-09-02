package org.example.unit;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;

@Getter
@Setter
public class PojoClass {
    private String name;
    private int value;
    private String[] stringArray;

    public PojoClass() {
    }

    @Override
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        }
        if (object == this) {
            return true;
        }
        if (object instanceof PojoClass pojo) {
            return pojo.name.equals(name) &&
                   pojo.value == value &&
                    Arrays.equals(pojo.stringArray, stringArray);
        }
        return false;
    }
}