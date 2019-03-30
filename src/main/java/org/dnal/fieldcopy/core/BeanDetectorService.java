package org.dnal.fieldcopy.core;

/**
 * Determines if a class is a bean or not.
 * Used when we need to apply transitive features to sub-objects.
 * 
 * A bean is a class with fields that FieldCopy needs to copy, such
 * as a Customer class.
 * 
 * Java Primitive objects and standards objects (String, etc) are not beans.
 * 
 * @author Ian Rae
 *
 */
public interface BeanDetectorService {
	boolean isBeanClass(Class<?> clazz);
}