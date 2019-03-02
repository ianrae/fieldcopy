package org.dnal.fc.core;

public interface ValueTransformer {
	boolean canHandle(String srcFieldName, Object value, Class<?> destClass);
	Object transformValue(String srcFieldName, Object srcBean, Object value, Class<?> destClass);
	void setCopySvc(FieldCopyService copySvc);
}
