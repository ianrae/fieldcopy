package org.dnal.fieldcopy.planner;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;

public class ZFieldPlan {
	public FieldDescriptor srcFd;
	public FieldDescriptor destFd;
	public ValueConverter converter;
	public Object defaultValue = null;
	
	public boolean isBean;
	public ZClassPlan subPlan; //null if not-bean
	//public boolean directMode; //later when we support plan backoff
	public boolean lazySubPlanFlag = false; 

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