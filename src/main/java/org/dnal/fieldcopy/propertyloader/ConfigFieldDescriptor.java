package org.dnal.fieldcopy.propertyloader;

import org.dnal.fieldcopy.core.FieldDescriptor;

public class ConfigFieldDescriptor implements FieldDescriptor {
	private String name;
	
	public ConfigFieldDescriptor(String name) {
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
}