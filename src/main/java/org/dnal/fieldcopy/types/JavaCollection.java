package org.dnal.fieldcopy.types;

import org.dnal.fieldcopy.util.StringUtil;

import java.util.Locale;

public enum JavaCollection {
    ARRAY,
    LIST, //TOOD: add ArrayList and some others
    SET,
    MAP;

    //INT -> int
    public static String lowify(JavaCollection prim) {
        String s = prim.name().toLowerCase(Locale.ROOT);
        return s;
    }
    public static String uppify(JavaCollection prim) {
        String s = prim.name().toLowerCase(Locale.ROOT);
        return StringUtil.uppify(s);
    }

    public static String getVarType(JavaCollection prim, String varType, String mapValueType) {
        switch (prim) {
            case ARRAY:
                return String.format("%s[]", varType);
            case LIST:
                return String.format("List<%s>", varType);
            case SET:
                return String.format("Set<%s>", varType);
            case MAP:
                return String.format("Map<%s,%s>", varType, mapValueType);
            default:
                return "?";
        }
    }

    public static String getImport(JavaCollection prim) {
        switch (prim) {
            case ARRAY:
                return null;
            case LIST:
                return "java.util.List";
            case SET:
                return "java.util.Set";
            case MAP:
                return "java.util.Map";
            default:
                return null;
        }
    }

}
