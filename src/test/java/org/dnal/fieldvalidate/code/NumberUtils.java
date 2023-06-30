package org.dnal.fieldvalidate.code;

public class NumberUtils {

    public static Integer asInt(Object minObj) {
        Number num = (Number)minObj;
        return num.intValue();
    }
    public static Long asLong(Object minObj) {
        Number num = (Number)minObj;
        return num.longValue();
    }
    public static Float asFloat(Object minObj) {
        Number num = (Number)minObj;
        return num.floatValue();
    }
    public static Double asDouble(Object minObj) {
        Number num = (Number)minObj;
        return num.doubleValue();
    }

}
