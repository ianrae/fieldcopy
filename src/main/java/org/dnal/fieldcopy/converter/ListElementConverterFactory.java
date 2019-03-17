package org.dnal.fieldcopy.converter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ListElementConverterFactory {

	
	public ListElementConverter createListConverter(String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		return new ListElementConverter(name, srcElementClass, destElementClass);
	}
	public ArrayElementConverter createArrayConverter(String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		return new ArrayElementConverter(name, srcElementClass, destElementClass);
	}

	private boolean isSupported(Class<?> srcElementClass, Class<?> destElementClass) {
		if (srcElementClass.equals(destElementClass)) {
			return true;
		}
		
		if (srcElementClass.equals(Date.class)) {
			List<Class<?>> list = Arrays.asList(String.class, Long.class);
			return list.contains(destElementClass);
		} else if (srcElementClass.isEnum()) {
			List<Class<?>> list = Arrays.asList(String.class);
			return list.contains(destElementClass);
		}
		return !destElementClass.isEnum();
	}
	
}
