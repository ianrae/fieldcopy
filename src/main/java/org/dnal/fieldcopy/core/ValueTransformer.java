package org.dnal.fieldcopy.core;

public interface ValueTransformer {
	boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass);
	Object transformValue(String srcFieldName, Object srcBean, Object value, Class<?> destClass);
	void setCopySvc(FieldCopyService copySvc);
}
