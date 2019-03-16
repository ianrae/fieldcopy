package org.dnal.fieldcopy.converter;

public interface ValueConverter {
	boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass);
	Object convertValue(Object srcBean, Object value, ConverterContext ctx);
}
