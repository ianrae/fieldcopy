package org.dnal.fieldcopy.service.beanutils;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.FieldDescriptor;

/**
 * Copy plan for a single field that is being copied from a source object to a 
 * destination object.
 * 
 * @author Ian Rae
 *
 */
public class BUFieldPlan {
	public FieldDescriptor srcFd;
	public FieldDescriptor destFd;
	public ValueConverter converter;
	public Object defaultValue = null;
	
	public boolean isBean;
	public volatile BUClassPlan subPlan; //null if not-bean
	//public boolean directMode; //later when we support plan backoff
	public volatile boolean lazySubPlanFlag = false; 

	public Class<?> getSrcClass() {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) srcFd;
		Class<?> srcClass = fd1.pd.getPropertyType();
		return srcClass;
	}
	public Class<?> getDestClass() {
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) destFd;
		Class<?> destClass = fd2.pd.getPropertyType();
		return destClass;
	}
}