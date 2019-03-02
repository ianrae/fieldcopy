package org.dnal.fc.core;

public class DefaultFieldFilter implements FieldFilter {

	@Override
	public boolean shouldProcess(Class<?> clazz, String fieldName) {
        if ("class".equals(fieldName)) {
        	return true; // No point in trying to set an object's class
        }
        return true;
	}
}
