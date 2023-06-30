package org.dnal.fieldcopy.types;

import org.dnal.fieldcopy.util.StringUtil;

import java.util.Locale;

// 8 of them
public enum JavaPrimitive {
    INT,
    BYTE,
    SHORT,
    LONG,
    FLOAT,
    DOUBLE,
    BOOLEAN,
    CHAR;

    //INT -> int
    public static String lowify(JavaPrimitive prim) {
        String s = prim.name().toLowerCase(Locale.ROOT);
        return s;
    }
    //INT -> Int
    public static String uppify(JavaPrimitive prim) {
        String s = prim.name().toLowerCase(Locale.ROOT);
        return StringUtil.uppify(s);
    }

    public static String getScalarType(JavaPrimitive prim) {
        switch(prim) {
            case INT:
                return "Integer";
            case BYTE:
                return "Byte";
            case SHORT:
                return "Short";
            case LONG:
                return "Long";
            case FLOAT:
                return "Float";
            case DOUBLE:
                return "Double";
            case BOOLEAN:
                return "Boolean";
            case CHAR:
                    return "Character";
            default:
                return "?";
        }
    }

    public static String getGetValueStr(JavaPrimitive prim) {
        switch(prim) {
            case INT:
                return "intValue";
            case BYTE:
                return "byteValue";
            case SHORT:
                return "shortValue";
            case LONG:
                return "longValue";
            case FLOAT:
                return "floatValue";
            case DOUBLE:
                return "doubleValue";
            case BOOLEAN:
                return "booleanValue";
            case CHAR:
                return "chaValue"; //TODO is this right??
            default:
                return "?";
        }
    }
}
