package org.dnal.fieldcopy.converter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ListElementConverterFactory {

	
	public ListElementConverter createConverter(String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		return new ListElementConverter(name, srcElementClass, destElementClass);
	}

	private boolean isSupported(Class<?> srcElementClass, Class<?> destElementClass) {
		if (srcElementClass.equals(destElementClass)) {
			return true;
		}
		
		if (srcElementClass.equals(Date.class)) {
			List<Class<?>> list = Arrays.asList(String.class, Long.class);
			return list.contains(destElementClass);
		}
		return true;
	}
	
}
