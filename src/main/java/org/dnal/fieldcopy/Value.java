package org.dnal.fieldcopy;

/**
 * 
 * @author Ian Rae
 *
 */
public interface Value {
	int getValueType();
	String name();
	void setNameInternal(String name);
	Object getRawObject();
	void setRawObject(Object val);
	boolean isNull();
}