package org.dnal.fieldcopy.converter;

public class FieldInfo {
	public Class<?> beanClass;
	public String fieldName;
	public Class<?> fieldClass;
	public boolean isArray; //field is an array
	
	public boolean matches(String fieldName) {
		return this.fieldName.equals(fieldName);
	}
	public boolean matches(Class<?> beanClass, String fieldName) {
		return this.beanClass.equals(beanClass) &&
				this.fieldName.equals(fieldName);
	}
}
