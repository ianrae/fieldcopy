package org.dnal.fieldcopy.planner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.service.beanutils.BUBeanDetectorService;

public class ZListElementConverterFactory {
	private BUBeanDetectorService beanDetectorSvc;
	private FieldCopyService outerSvc;

	public ZListElementConverterFactory(FieldCopyService outerSvc) {
		this.outerSvc = outerSvc;
	}
	
	public ZListElementConverter createListConverter(Class<?> beanClass, String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		boolean useScalarCopy = ! beanDetectorSvc.isBeanClass(srcElementClass) && ! beanDetectorSvc.isBeanClass(destElementClass);
		List<FieldPair> fieldPairs = null;
		if (!useScalarCopy) {
			fieldPairs = outerSvc.buildAutoCopyPairs(srcElementClass, destElementClass);
		}
		
		return new ZListElementConverter(beanClass, name, srcElementClass, destElementClass, useScalarCopy, fieldPairs);
	}
	public ZArrayElementConverter createArrayConverter(Class<?> beanClass, String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		boolean useScalarCopy = ! beanDetectorSvc.isBeanClass(srcElementClass) && ! beanDetectorSvc.isBeanClass(destElementClass);
		List<FieldPair> fieldPairs = null;
		if (!useScalarCopy) {
			fieldPairs = outerSvc.buildAutoCopyPairs(srcElementClass, destElementClass);
		}
		return new ZArrayElementConverter(beanClass, name, srcElementClass, destElementClass, useScalarCopy, fieldPairs);
	}

	private boolean isSupported(Class<?> srcElementClass, Class<?> destElementClass) {
		if (srcElementClass.equals(destElementClass)) {
			return true;
		}
		
		if (srcElementClass.equals(Date.class)) {
			List<Class<?>> list = Arrays.asList(String.class, Long.class);
			return list.contains(destElementClass);
		} else if (srcElementClass.isEnum()) {
			List<Class<?>> list = Arrays.asList(String.class);
			return list.contains(destElementClass);
		}
		return !destElementClass.isEnum();
	}
	public void setBeanDetectorSvc(BUBeanDetectorService beanDetectorSvc) {
		this.beanDetectorSvc = beanDetectorSvc;
	}
	
}
