package org.dnal.fc.core;

public interface ValueTransformer {
	boolean canHandle(Object value, Class<?> destClass);
	Object transformValue(Object value, Class<?> destClass);
}
