package org.dnal.fieldcopy.implicitconverter;

public class CastConverter implements ImplicitConverter {
    public Class<?> targetClass;

    public CastConverter(Class<?> targetClass) {
        this.targetClass = targetClass;
    }

    @Override
    public String gen(String varName) {
        Class<?> clazz = targetClass;
        return String.format("(%s)%s", clazz.getSimpleName(), varName);
    }
}
