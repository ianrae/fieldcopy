package org.dnal.fc.core;

import java.lang.reflect.Method;

public class FieldCopyUtils {
	public static String className(Class<?> clazz) {
		return clazz.getSimpleName();
	}
	public static String classNameForObj(Object obj) {
		return obj.getClass().getSimpleName();
	}
	public static String objToString(Object obj) {
		String tmp = (obj == null) ? "(null)" : obj.toString();
		return tmp;
	}
	
	//uses reflection
	public static Object createEnumObject(String name, Class<? extends Enum<?>> clazz) {
		Object value = null;
		try {
		    Method valueOf = clazz.getMethod("valueOf", String.class);
		    value = valueOf.invoke(null, name);
		} catch ( ReflectiveOperationException e) {
			throw new FieldCopyException("enum copy failed!: " + e.getMessage());
		}				
		return value;
	}
}