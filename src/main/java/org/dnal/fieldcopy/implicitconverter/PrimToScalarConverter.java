package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;

public class PrimToScalarConverter implements ImplicitConverter {
    private final JavaPrimitive destPrim;
    private final JavaPrimitive srcPrim;

    public PrimToScalarConverter(JavaPrimitive srcPrim, JavaPrimitive destPrim) {
        this.srcPrim = srcPrim;
        this.destPrim = destPrim;
    }

    @Override
    public String gen(String varName) {
        /*
    nn = Integer.valueOf(bb);
                return String.format("Math.toIntExact(%s)", varName); //TODO use this elswhere with LONG??
    nn = Math.toIntExact(ll);
    float ff = 55.6f;
    nn = Integer.valueOf((int) ff);
    char ch = 'a';
    nn = Integer.valueOf(ch);
         */
        if (JavaPrimitive.CHAR.equals(destPrim)) {
            return doCharGen(varName);
        } else if (JavaPrimitive.BYTE.equals(destPrim)) {
            return doByteGen(varName);
        }

        String scalar = JavaPrimitive.getScalarType(destPrim);
        String primStr = JavaPrimitive.lowify(destPrim);
        switch (srcPrim) {
            case FLOAT:
            case DOUBLE:
                return String.format("%s.valueOf((%s)%s)", scalar, primStr, varName);
            default:
                break;
        }

        return String.format("%s.valueOf(%s)", scalar, varName);
    }

    private String doByteGen(String varName) {
        String scalar = JavaPrimitive.getScalarType(destPrim);
        return String.format("Byte.valueOf((byte)%s)", varName);
    }

    private String doCharGen(String varName) {
        String scalar = JavaPrimitive.getScalarType(destPrim);
        return String.format("Character.valueOf((char)%s)", varName);
    }
}
