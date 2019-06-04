package org.dnal.fieldcopy.service.beanutils;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.SourceValueFieldDescriptor;

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
	public boolean hasSetterMethod;
	public boolean hasSetterMethodIsResolved;
	public boolean isBean;
	public volatile BUClassPlan subPlan; //null if not-bean
	//public boolean directMode; //later when we support plan backoff
	public volatile boolean lazySubPlanFlag = false; 
	
	public BUFieldPlan clone() {
		//TODO: always add here when add new field
		BUFieldPlan fp = new BUFieldPlan();
		fp.converter = this.converter;
		fp.defaultValue = this.defaultValue;
		fp.destFd = this.destFd;
		fp.hasSetterMethod = this.hasSetterMethod;
		fp.hasSetterMethodIsResolved = this.hasSetterMethodIsResolved;
		fp.isBean = this.isBean;
		fp.lazySubPlanFlag = this.lazySubPlanFlag;
		fp.srcFd = this.srcFd;
		fp.subPlan = this.subPlan;
		return fp;
	}

	public Class<?> getSrcClass() {
		if (srcFd instanceof SourceValueFieldDescriptor) {
			SourceValueFieldDescriptor svfd = (SourceValueFieldDescriptor) srcFd;
			return svfd.getValue().getClass();
		}
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