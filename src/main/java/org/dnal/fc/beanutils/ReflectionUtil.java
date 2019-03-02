package org.dnal.fc.beanutils;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Reflection helper
 * 
 * @author Ian Rae
 *
 */
public class ReflectionUtil {


	public static Class<?> detectElementClass(Object destObj, BeanUtilsFieldDescriptor fd2) {
		String name = fd2.getName();
		Field field = null;
		try {
			field = destObj.getClass().getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (field == null) {
			return null;
		}
		Type typ = field.getGenericType();
		if (typ != null) {
			if (typ instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) typ;
				Type[] argTypes = paramType.getActualTypeArguments();
				Type target = argTypes[0];
				Class<?> zz = null;
				try {
					zz = Class.forName(target.getTypeName());
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return zz;
			}		
		}

		return null;
	}

}