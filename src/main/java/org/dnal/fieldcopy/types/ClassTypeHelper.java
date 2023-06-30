package org.dnal.fieldcopy.types;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class ClassTypeHelper {

    public static boolean isStructType(FieldTypeInformation fieldInfo) {
        if (fieldInfo.isOptional()) {
            Class<?> clazz = fieldInfo.getFirstActual();
            return isStructType(clazz);
        }
        return isStructType(fieldInfo.getFieldType());
    }

    public static boolean isStructType(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return false;
        }
        if (clazz.isEnum()) {
            return false;
        }
        if (clazz.isArray()) {
            return false;
        }
        if (clazz.equals(String.class)) {
            return false;
        }

        //        //byte,short,int,long  float,double, boolean, char
        if (Number.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (Boolean.class.isAssignableFrom(clazz)) {
            return false;
        }
        if (Character.class.isAssignableFrom(clazz)) {
            return false;
        }
        //TODO lists,maps later
        //TODO BigNumber
        return true;
    }

    public static boolean isPrimitive(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return true;
        }
        return false;
    }

    public static boolean isStringType(Class<?> clazz) {
        if (clazz.equals(String.class)) {
            return true;
        }
        return false;
    }

    public static boolean isEnumType(Class<?> clazz) {
        if (clazz.isEnum()) {
            return true;
        }
        return false;
    }

    public static boolean isDateType(Class<?> clazz) {
        if (Date.class.isAssignableFrom(clazz)) {
            return true;
        } else if (LocalDate.class.isAssignableFrom(clazz)) {
            return true;
        } else if (LocalTime.class.isAssignableFrom(clazz)) {
            return true;
        } else if (LocalDateTime.class.isAssignableFrom(clazz)) {
            return true;
        } else if (ZonedDateTime.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }
    public static boolean isLongType(Class<?> clazz) {
        if (Long.class.isAssignableFrom(clazz)) {
            return true;
        } else if (long.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }
    public static boolean isFloatType(Class<?> clazz) {
        if (Float.class.isAssignableFrom(clazz)) {
            return true;
        } else if (float.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }
    public static boolean isDoubleType(Class<?> clazz) {
        if (Double.class.isAssignableFrom(clazz)) {
            return true;
        } else if (double.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }
    public static boolean isCharType(Class<?> clazz) {
        if (Character.class.isAssignableFrom(clazz)) {
            return true;
        } else if (char.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }

    public static boolean isListType(Class<?> clazz) {
        if (List.class.isAssignableFrom(clazz)) {
            return true;
        }
        return false;
    }

}
