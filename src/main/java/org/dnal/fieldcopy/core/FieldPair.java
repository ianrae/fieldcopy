package org.dnal.fieldcopy.core;

/**
 * Defines the copy of a source field to a destination field.
 * 
 * @author Ian Rae
 *
 */
public class FieldPair {
	public FieldDescriptor srcProp;
	public String destFieldName;
	public FieldDescriptor destProp; //set lazily
	public Object defaultValue = null; //use if src field is null
}