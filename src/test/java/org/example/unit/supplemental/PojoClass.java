package org.example.unit.supplemental;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class PojoClass {

    private String name;
    private int value;
    private String[] stringArray;
    private List<Integer> integerList;
    private Map<String, Boolean> stringBooleanMap;
    private NestedPojoClass nestedPojoObject;

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
                   Arrays.equals(pojo.stringArray, stringArray) &&
                   pojo.integerList.equals(integerList) &&
                   pojo.stringBooleanMap.equals(stringBooleanMap);
        }
        return false;
    }
}