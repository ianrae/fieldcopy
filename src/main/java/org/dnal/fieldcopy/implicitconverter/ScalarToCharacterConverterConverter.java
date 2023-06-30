package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;

public class ScalarToCharacterConverterConverter implements ImplicitConverter {
    private final JavaPrimitive prim;

    public ScalarToCharacterConverterConverter(JavaPrimitive prim) {
        this.prim = prim;
    }

    @Override
    public String gen(String varName) {
        String scalar = JavaPrimitive.getScalarType(prim);
        String primStr = JavaPrimitive.uppify(prim);
        //        cch = Character.valueOf((char) n.intValue());
        return String.format("Character.valueOf((char)%s.intValue())", varName);
    }
}
