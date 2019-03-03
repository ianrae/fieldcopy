package org.dnal.fieldcopy.converter;

import org.dnal.fieldcopy.core.FieldCopyService;

public interface ValueConverter {
	boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass);
	Object convertValue(Object srcBean, Object value, ConverterContext ctx);
	void setCopySvc(FieldCopyService copySvc);
}
