package org.dnal.fc.core;

/**
 * Ignores the class field (i.e. getClass) which should not be copied.
 * 
 * @author Ian Rae
 *
 */
public class DefaultFieldFilter implements FieldFilter {

	@Override
	public boolean shouldProcess(Class<?> clazz, String fieldName) {
        if ("class".equals(fieldName)) {
        	return false; // No point in trying to set an object's class
        }
        return true;
	}
}
