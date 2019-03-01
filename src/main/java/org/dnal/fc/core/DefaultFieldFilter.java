package org.dnal.fc.core;

public class DefaultFieldFilter implements FieldFilter {

	@Override
	public boolean shouldProcess(Object src, String fieldName) {
        if ("class".equals(fieldName)) {
        	return true; // No point in trying to set an object's class
        }
        return true;
	}
}
