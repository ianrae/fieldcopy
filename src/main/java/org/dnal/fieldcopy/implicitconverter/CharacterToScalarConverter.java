package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;

public class CharacterToScalarConverter implements ImplicitConverter {
    private final JavaPrimitive prim;

    public CharacterToScalarConverter(JavaPrimitive prim) {
        this.prim = prim;
    }

    @Override
    public String gen(String varName) {
        String scalar = JavaPrimitive.getScalarType(prim);
        String primStr = JavaPrimitive.uppify(prim);
        // n = Integer.valueOf(cch.charValue());
        return String.format("%s.valueOf(%s.charValue())", scalar, varName);
    }
}
