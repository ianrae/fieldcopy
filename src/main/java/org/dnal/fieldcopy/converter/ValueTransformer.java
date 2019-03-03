package org.dnal.fieldcopy.converter;

import org.dnal.fieldcopy.core.FieldCopyService;

public interface ValueTransformer {
	boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass);
	Object transformValue(Object srcBean, Object value, ConverterContext ctx);
	void setCopySvc(FieldCopyService copySvc);
}
