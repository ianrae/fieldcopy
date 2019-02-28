package org.dnal.fc.core;

public interface AutoCopyFieldFilter {
	boolean shouldCopy(Object src, String fieldName);
}
