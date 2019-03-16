package org.dnal.fieldcopy.service.beanutils;

import java.lang.reflect.Array;
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
	
	public static boolean elementIsList(Object destObj, BeanUtilsFieldDescriptor fd2) {
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
			return false;
		}
		
		Type typ = field.getGenericType();
		if (typ != null) {
			if (typ instanceof ParameterizedType) {
				ParameterizedType paramType = (ParameterizedType) typ;
				Type[] argTypes = paramType.getActualTypeArguments();
				Type target = argTypes[0];
				
				String s = target.getTypeName();
				if (s.startsWith("java.util.List")) {
					return true;
				}
			}		
		}
		
		return false;
	}

	public static ListSpec buildListSpec(Object destObj, BeanUtilsFieldDescriptor fd2) {
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
		if (typ == null) {
			return null;
		}
		ListSpec spec = new ListSpec();
		return buildListInfo(typ, spec);
	}

	private static ListSpec buildListInfo(Type typ, ListSpec spec) {
		if (typ instanceof ParameterizedType) {
			ParameterizedType paramType = (ParameterizedType) typ;
			Type[] argTypes = paramType.getActualTypeArguments();
			Type target = argTypes[0];

			String typeName = target.getTypeName();
			String targetStr = "java.util.List<";
			if (typeName.startsWith(targetStr)) {
				typeName = typeName.substring(targetStr.length(), typeName.length() - 1);
				
				while (typeName.startsWith(targetStr)) {
					spec.depth++;
					typeName = typeName.substring(targetStr.length(), typeName.length() - 1);
				}
				spec.elementClass = safeGetClass(typeName);
				spec.depth++;
				return buildListInfo(spec.elementClass, spec);
			} else {
				spec.elementClass = safeGetClass(typeName);
			}
		}		
		
		return spec;
	}
	
	public static ListSpec buildArraySpec(Object destObj, BeanUtilsFieldDescriptor fd2) {
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
		
		Class<?> typ = field.getType();
		if (typ == null || !typ.isArray()) {
			return null;
		}
		ListSpec spec = new ListSpec();
		return buildArrayInfo(typ, spec);
	}

	private static ListSpec buildArrayInfo(Class<?> typ, ListSpec spec) {
		spec.depth = 0;
		spec.elementClass = typ.getComponentType();
		
		//TODO handle arrays of arrays
		return spec;
	}
	
	private static Class<?> safeGetClass(String className) {
		Class<?> zz = null;
		try {
			zz = Class.forName(className);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zz;
	}
	
}