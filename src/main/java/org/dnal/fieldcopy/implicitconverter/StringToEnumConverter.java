package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.util.ReflectionUtil;

public class StringToEnumConverter implements ImplicitConverter {
    private final Class<?> enumClass;
    private final ReflectionUtil helper;

    public StringToEnumConverter(Class<?> enumClass) {
        this.enumClass = enumClass;
        this.helper = new ReflectionUtil();
    }

    @Override
    public String gen(String varName) {
        String s = String.format("Enum.valueOf(%s.class, %s)", enumClass.getName(), varName);
        return s;
    }
}
