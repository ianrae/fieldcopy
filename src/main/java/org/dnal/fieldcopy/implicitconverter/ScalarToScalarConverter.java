package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;

public class ScalarToScalarConverter implements ImplicitConverter {
    private final JavaPrimitive prim;

    public ScalarToScalarConverter(JavaPrimitive prim) {
        this.prim = prim;
    }

    @Override
    public String gen(String varName) {
        String scalar = JavaPrimitive.getScalarType(prim);
        String primStr = JavaPrimitive.lowify(prim);
        //        Integer n = by.intValue();
        //TODO when BYTE we need to cast varName to byte
        return String.format("%s.%sValue()", varName, primStr);
    }
}
