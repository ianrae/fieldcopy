package org.dnal.fieldcopy.util;

import org.apache.commons.lang3.StringUtils;

public class ClassNameUtil {

    public static String renderClassName(Class<?> clazz) {
        return renderClassName(clazz.getName());
    }
    public static String renderClassName(String className) {
        String target = "java.lang.";
        if (className.startsWith(target)) {
            return StringUtils.substringAfter(className, target);
        }
        target = "java.util.";
        if (className.startsWith(target)) {
            return StringUtils.substringAfter(className, target);
        }
        return className;
    }
}
