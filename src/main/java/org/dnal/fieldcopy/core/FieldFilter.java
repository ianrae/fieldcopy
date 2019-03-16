package org.dnal.fieldcopy.core;

/**
 * A filter that determines which fields should be copied.
 * Useful for copying ORM objects that have internal fields that you don't want
 * to copy.  To ignore those, create a custom FieldFilter.
 * 
 * @author Ian Rae
 *
 */
public interface FieldFilter {
	boolean shouldProcess(Class<?> clazz, String fieldName);
}
