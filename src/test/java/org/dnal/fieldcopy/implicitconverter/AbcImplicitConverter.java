package org.dnal.fieldcopy.implicitconverter;

public class AbcImplicitConverter implements ImplicitConverter {
    @Override
    public String gen(String varName) {
        return "abc";
    }
}
