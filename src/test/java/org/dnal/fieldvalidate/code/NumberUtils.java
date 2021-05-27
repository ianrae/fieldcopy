package org.dnal.fieldvalidate.code;

public class NumberUtils {

    public static Integer asInt(Object minObj) {
        Number num = (Number)minObj;
        return num.intValue();
    }

}
