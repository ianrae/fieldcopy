package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;

public class StringToPrimConverter implements ImplicitConverter {
    private final JavaPrimitive prim;

    public StringToPrimConverter(JavaPrimitive prim) {
        this.prim = prim;
    }

    @Override
    public String gen(String varName) {
        String scalar = JavaPrimitive.getScalarType(prim);
        String primStr = JavaPrimitive.uppify(prim);

        return String.format("%s.parse%s(%s)", scalar, primStr, varName);
    }
}
