package org.dnal.fieldcopy.converter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldPair;

public class ListElementConverter implements ValueConverter {
	private String srcFieldName;
	private Class<?> srcElClass;
	private Class<?> destElClass;
	private List<Class<?>> knownScalarsL;
	
	public ListElementConverter(String fieldName, Class<?> srcElementClass, Class<?> destElementClass) {
		this.srcFieldName = fieldName;
		this.srcElClass = srcElementClass;
		this.destElClass = destElementClass;
		this.knownScalarsL = Arrays.asList(String.class, Date.class);
	}

	@Override
	public boolean canConvert(String fieldName, Class<?>srcClass, Class<?> destClass) {
		return this.srcFieldName.equals(fieldName);
	}
	
	@Override
	public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
		if (value == null) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		List<?> list = (List<?>) value;
		
		//TODO: why can't we determine srcElClass during generation?
		//Class<?> srcElClass = this.detectSrcElementClass(srcBean);
		if (! isBean(srcElClass) && ! isBean(destElClass)) {
			return copyScalarList(list, srcElClass);
		}
		List<FieldPair> fieldPairs = ctx.copySvc.buildAutoCopyPairs(srcElClass, destElClass);

		CopySpec spec = new CopySpec();
		spec.fieldPairs = fieldPairs;
		spec.options = ctx.copyOptions;
		spec.mappingL = null;
		spec.converterL = null;

		List<Object> list2 = new ArrayList<>();
		for(Object el: list) {
			spec.sourceObj = el;
			spec.destObj = createObject(destElClass);
			ctx.copySvc.copyFields(spec);
			
			list2.add(spec.destObj);
		}
		return list2;
	}
	
	private Object copyScalarList(List<?> list, Class<?> srcElClass) {
		List<Object> list2 = new ArrayList<>();
		for(Object el: list) {
			
			Object result = ConvertUtils.convert(el, destElClass);
			list2.add(result);
		}
		return list2;
	}

	/**
	 * Determine if class is a bean (i.e. has inner fields).
	 * Classes like Integer are not beans.
	 * @param clazz
	 * @return
	 */
	private boolean isBean(Class<?> clazz) {
		if (knownScalarsL.contains(clazz) || clazz.isEnum()) {
			return false;
		}
		
		try {
			//use Java's Introspector. beans will have > 1 property descriptor
			BeanInfo info = Introspector.getBeanInfo(clazz);
			return info.getPropertyDescriptors().length > 1;
		} catch (IntrospectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private Object createObject(Class<?> clazzDest) {
		Object obj = null;
		try {
			obj = clazzDest.newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}

}