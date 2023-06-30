package org.dnal.fieldcopy.implicitconverter;

public class EnumToStringConverter implements ImplicitConverter {
    private final Class<?> enumClass;

    public EnumToStringConverter(Class<?> enumClass) {
        this.enumClass = enumClass;
    }

    @Override
    public String gen(String varName) {
        return String.format("%s.name()", varName);
    }
}
