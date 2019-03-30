package org.dnal.fieldcopy.planner;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.dnal.fieldcopy.converter.ArrayElementConverter;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsBeanDetectorService;

public class ZListElementConverterFactory {
	private BeanUtilsBeanDetectorService beanDetectorSvc;

	
	public ZListElementConverter createListConverter(Class<?> beanClass, String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		boolean useScalarCopy = ! beanDetectorSvc.isBeanClass(srcElementClass) && ! beanDetectorSvc.isBeanClass(destElementClass);
		return new ZListElementConverter(beanClass, name, srcElementClass, destElementClass, useScalarCopy);
	}
	public ArrayElementConverter createArrayConverter(Class<?> beanClass, String name, Class<?> srcElementClass, Class<?> destElementClass) {
		if (! isSupported(srcElementClass, destElementClass)) {
			return null;
		}
		
		return new ArrayElementConverter(beanClass, name, srcElementClass, destElementClass, beanDetectorSvc);
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
	public void setBeanDetectorSvc(BeanUtilsBeanDetectorService beanDetectorSvc) {
		this.beanDetectorSvc = beanDetectorSvc;
	}
	
}
