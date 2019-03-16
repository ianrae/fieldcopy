package org.dnal.fieldcopy.converter;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldPair;

/**
 * Converts the elements of an array.
 * 
 * This is a special converter where the fieldName passed to canConvert is the name of the
 * field that is an array, but the srcClass is the type of the elements of the array.
 * 
 * @author Ian Rae
 *
 */
public class ArrayElementConverter implements ValueConverter {
	private String srcFieldName;
	private Class<?> srcElClass;
	private Class<?> destElClass;
	private List<Class<?>> knownScalarsL;
	private boolean useScalarCopy;
	private int depth;
	
	public ArrayElementConverter(String fieldName, Class<?> srcElementClass, Class<?> destElementClass) {
		this.srcFieldName = fieldName;
		this.srcElClass = srcElementClass;
		this.destElClass = destElementClass;
		this.knownScalarsL = Arrays.asList(String.class, Date.class);
		this.useScalarCopy = ! isBean(srcElClass) && ! isBean(destElClass);
	}

	@Override
	public boolean canConvert(FieldInfo source, FieldInfo dest) {
		return source.matches(srcFieldName);
	}
	
	@Override
	public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
		if (value == null) {
			return null;
		}
		
		Object srcArray = value;
		
		if (depth == 0) {
			return copyInnerMostArray(srcArray, ctx);
		} else {
//			List<Object> list2 = new ArrayList<>();
//			for(Object el: list) {
//				List<?> inner = (List<?>) el;
//				List<?> innerCopy = copyNextLevelNestedList(inner, ctx, 0);
//				list2.add(innerCopy);
//			}
//			return list2;
			return null; //TODO FIX
		}
	}
	
//	private List<?> copyNextLevelNestedList(List<?> list, ConverterContext ctx, int currentDepth) {
//		boolean isInnermost = (currentDepth == depth - 1);
//		if (isInnermost) {
//			return copyInnerMostList(list, ctx);
//		} else {
//			List<Object> list2 = new ArrayList<>();
//			for(Object el: list) {
//				List<?> inner = (List<?>) el;
//				List<?> innerCopy = copyNextLevelNestedList(inner, ctx, currentDepth + 1);
//				list2.add(innerCopy);
//			}
//			return list2;
//		}
//	}

	private Object copyInnerMostArray(Object srcArray, ConverterContext ctx) {
		if (useScalarCopy) {
			return copyScalarArray(srcArray, srcElClass);
		}
		List<FieldPair> fieldPairs = ctx.copySvc.buildAutoCopyPairs(srcElClass, destElClass);

		CopySpec spec = new CopySpec();
		spec.fieldPairs = fieldPairs;
		spec.options = ctx.copyOptions;
		spec.mappingL = null;
		spec.converterL = null;

		Object arrayObj2 = Array.newInstance(destElClass, 0);
		int n = Array.getLength(srcArray);
		for(int i = 0; i < n; i++) {
			Object el = Array.get(srcArray, i);
			spec.sourceObj = el;
			spec.destObj = createObject(destElClass);
			ctx.copySvc.copyFields(spec);
			
			Array.set(arrayObj2, i, spec.destObj);
		}
		return arrayObj2;
	}

	private Object copyScalarArray(Object srcArray, Class<?> srcElClass) {
		Object arrayObj2 = Array.newInstance(destElClass, 0);
		
		int n = Array.getLength(srcArray);
		for(int i = 0; i < n; i++) {
			Object el = Array.get(srcArray, i);
			Object result = ConvertUtils.convert(el, destElClass);
			
			Array.set(arrayObj2, i, result);
		}
		return arrayObj2;
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

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

}