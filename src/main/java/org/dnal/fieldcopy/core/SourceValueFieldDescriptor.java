package org.dnal.fieldcopy.core;


import org.dnal.fieldcopy.core.FieldDescriptor;

/**
 * Field description for BeanUtils copy service.
 * 
 * @author Ian Rae
 *
 */
public class SourceValueFieldDescriptor implements FieldDescriptor {
	
	private String name;
	private Object value;

	public SourceValueFieldDescriptor(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
}