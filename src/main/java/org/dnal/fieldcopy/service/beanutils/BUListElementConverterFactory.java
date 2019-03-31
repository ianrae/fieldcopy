package org.dnal.fieldcopy.service.beanutils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;

public class BUListElementConverterFactory {
	private BUBeanDetectorService beanDetectorSvc;
	private FieldCopyService outerSvc;

	public BUListElementConverterFactory(FieldCopyService outerSvc) {
		this.outerSvc = outerSvc;
	}
	
	public BUListElementConverter createListConverter(Class<?> beanClass, String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		boolean useScalarCopy = ! beanDetectorSvc.isBeanClass(srcElementClass) && ! beanDetectorSvc.isBeanClass(destElementClass);
		List<FieldPair> fieldPairs = null;
		if (!useScalarCopy) {
			fieldPairs = outerSvc.buildAutoCopyPairs(srcElementClass, destElementClass);
		}
		
		return new BUListElementConverter(beanClass, name, srcElementClass, destElementClass, useScalarCopy, fieldPairs);
	}
	public BUArrayElementConverter createArrayConverter(Class<?> beanClass, String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		boolean useScalarCopy = ! beanDetectorSvc.isBeanClass(srcElementClass) && ! beanDetectorSvc.isBeanClass(destElementClass);
		List<FieldPair> fieldPairs = null;
		if (!useScalarCopy) {
			fieldPairs = outerSvc.buildAutoCopyPairs(srcElementClass, destElementClass);
		}
		return new BUArrayElementConverter(beanClass, name, srcElementClass, destElementClass, useScalarCopy, fieldPairs);
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
