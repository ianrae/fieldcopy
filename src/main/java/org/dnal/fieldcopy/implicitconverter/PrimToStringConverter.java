package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;

public class PrimToStringConverter implements ImplicitConverter {
    private final JavaPrimitive prim;

    public PrimToStringConverter(JavaPrimitive prim) {
        this.prim = prim;
    }

    @Override
    public String gen(String varName) {
        //        s = Integer.valueOf(44).toString();
        String scalar = JavaPrimitive.getScalarType(prim);

        return String.format("%s.valueOf(%s).toString()", scalar, varName);
    }
}
