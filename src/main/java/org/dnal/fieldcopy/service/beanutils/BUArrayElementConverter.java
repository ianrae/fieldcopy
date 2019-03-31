package org.dnal.fieldcopy.service.beanutils;

import java.lang.reflect.Array;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
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
public class BUArrayElementConverter implements ValueConverter {
	private String srcFieldName;
	private Class<?> srcElClass;
	private Class<?> destElClass;
	private boolean useScalarCopy;
	private int depth;
	private Class<?> beanClass;
	private boolean sourceIsList;
	private List<FieldPair> fieldPairs;

	public BUArrayElementConverter(Class<?> beanClass, String fieldName, Class<?> srcElementClass, Class<?> destElementClass,
			boolean useScalarCopy, List<FieldPair> fieldPairs) {
		this.beanClass = beanClass;
		this.srcFieldName = fieldName;
		this.srcElClass = srcElementClass;
		this.destElClass = destElementClass;
		this.useScalarCopy = useScalarCopy;
		this.fieldPairs = fieldPairs;
	}

	@Override
	public boolean canConvert(FieldInfo source, FieldInfo dest) {
		return source.matches(beanClass, srcFieldName);
	}
	
	@Override
	public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
		if (value == null) {
			return null;
		}
		
		Object srcArray = getAsArray(value);
		
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

	private Object getAsArray(Object value) {
		if (sourceIsList) {
			List<?> list = (List<?>) value;
			return list.toArray();
		} else {
			return value;
		}
	}

	private Object copyInnerMostArray(Object srcArray, ConverterContext ctx) {
		if (useScalarCopy) {
			return copyScalarArray(srcArray);
		}
		CopySpec spec = new CopySpec();
		spec.fieldPairs = fieldPairs;
		spec.options = ctx.copyOptions;
		spec.mappingL = ctx.mappingL;
		spec.converterL = ctx.converterL;
		spec.runawayCounter = ctx.runawayCounter;

		int n = Array.getLength(srcArray);
		Object arrayObj2 = Array.newInstance(destElClass, n);
		Class<?> prevSrcClass = null;
		for(int i = 0; i < n; i++) {
			Object el = Array.get(srcArray, i);
			spec.sourceObj = el;
			spec.destObj = createObject(destElClass);
			ctx.copySvc.copyFields(spec);
			
			Array.set(arrayObj2, i, spec.destObj);
			
			if (prevSrcClass == null || prevSrcClass.equals(el.getClass())) {
			} else {
				spec.executionPlanCacheKey = null; //clear so re-calc key on each element
			}
			prevSrcClass = el.getClass();
		}
		return arrayObj2;
	}

	private Object copyScalarArray(Object srcArray) {
		int n = Array.getLength(srcArray);
		Object arrayObj2 = Array.newInstance(destElClass, n);
		
		for(int i = 0; i < n; i++) {
			Object el = Array.get(srcArray, i);
			Object result = ConvertUtils.convert(el, destElClass);
			
			Array.set(arrayObj2, i, result);
		}
		return arrayObj2;
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

	public boolean isSourceIsList() {
		return sourceIsList;
	}

	public void setSourceIsList(boolean sourceIsList) {
		this.sourceIsList = sourceIsList;
	}

}