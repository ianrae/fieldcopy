package org.dnal.fc.beanutils;

import java.beans.PropertyDescriptor;

import org.dnal.fc.core.FieldDescriptor;

public class BeanUtilsFieldDescriptor implements FieldDescriptor {
	public PropertyDescriptor pd;
	
	public BeanUtilsFieldDescriptor(PropertyDescriptor pd) {
		this.pd = pd;
	}
	
	@Override
	public String getName() {
		return pd.getName();
	}
	
}