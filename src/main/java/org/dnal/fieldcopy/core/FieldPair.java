package org.dnal.fieldcopy.core;

import org.dnal.fieldcopy.service.beanutils.old.BeanUtilsFieldDescriptor;

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
	
	public Class<?> getSrcClass() {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) srcProp;
		Class<?> srcClass = fd1.pd.getPropertyType();
		return srcClass;
	}
	public Class<?> getDestClass() {
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) destProp;
		Class<?> destClass = fd2.pd.getPropertyType();
		return destClass;
	}
}