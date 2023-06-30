package org.dnal.fieldcopy.implicitconverter;

import org.dnal.fieldcopy.types.JavaPrimitive;

public class ScalarToPrimConverter implements ImplicitConverter {
    private final JavaPrimitive destPrim;
    private final JavaPrimitive srcPrim;

    public ScalarToPrimConverter(JavaPrimitive srcPrim, JavaPrimitive destPrim) {
        this.srcPrim = srcPrim;
        this.destPrim = destPrim;
    }

    @Override
    public String gen(String varName) {
    /*        int n = n3.intValue();
    n = b3.intValue();
    n = sh3.intValue();

     */
        if (JavaPrimitive.CHAR.equals(destPrim)) {
            return doCharGen(varName);
        } else if (JavaPrimitive.SHORT.equals(destPrim)) {
            return doShortGen(varName);
        } else if (JavaPrimitive.BYTE.equals(destPrim)) {
            return doByteGen(varName);
        }

        String scalar = JavaPrimitive.getScalarType(destPrim);
        String primStr = JavaPrimitive.lowify(destPrim);
        switch (srcPrim) {
            case CHAR: //(short) c3.charValue();
                return String.format("%s.charValue()", varName);
            default:
                break;
        }

        return String.format("%s.%sValue()", varName, primStr);
    }

    private String doByteGen(String varName) {
        String scalar = JavaPrimitive.getScalarType(destPrim);
        return String.format("(byte)%s", varName);
    }

    private String doShortGen(String varName) {
        String scalar = JavaPrimitive.getScalarType(destPrim);
        String primStr = JavaPrimitive.lowify(destPrim);
        switch (srcPrim) {
            case CHAR: //(short) c3.charValue();
                return String.format("(short) %s.charValue()", varName);
            default:
                break;
        }
        return String.format("%s.%sValue()", varName, primStr);
    }


    //        sh = (short) c3.charValue();
    private String doCharGen(String varName) {
        String scalar = JavaPrimitive.getScalarType(destPrim);
        String primStr = JavaPrimitive.lowify(destPrim);
//            return String.format("(char) %s.intValue()", varName);
        return String.format("(%s) %s.intValue()", primStr, varName);
    }
}
