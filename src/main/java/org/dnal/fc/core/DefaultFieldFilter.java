package org.dnal.fc.core;

public class DefaultFieldFilter implements AutoCopyFieldFilter {

	@Override
	public boolean shouldCopy(Object src, String fieldName) {
        if ("class".equals(fieldName)) {
        	return false; // No point in trying to set an object's class
        }
        return true;
	}
}
