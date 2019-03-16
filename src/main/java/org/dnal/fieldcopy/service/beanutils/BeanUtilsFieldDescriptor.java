package org.dnal.fieldcopy.service.beanutils;

import java.beans.PropertyDescriptor;

import org.dnal.fieldcopy.core.FieldDescriptor;

/**
 * Field description for BeanUtils copy service.
 * 
 * @author Ian Rae
 *
 */
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