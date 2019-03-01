package org.dnal.fc.core;

public interface FieldFilter {
	boolean shouldProcess(Object src, String fieldName);
}
