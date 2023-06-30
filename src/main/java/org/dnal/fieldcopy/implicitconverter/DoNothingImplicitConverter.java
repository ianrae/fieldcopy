package org.dnal.fieldcopy.implicitconverter;

public class DoNothingImplicitConverter implements ImplicitConverter {
    @Override
    public String gen(String varName) {
        return null;
    }
}
