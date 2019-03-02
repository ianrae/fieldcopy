package org.dnal.fc.core;

public interface FieldFilter {
	boolean shouldProcess(Class<?> clazz, String fieldName);
}
