package org.dnal.fieldcopy.service.beanutils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.util.ThreadSafeList;

/**
 * Converts the elements of a list.
 * 
 * This is a special converter where the fieldName passed to canConvert is the name of the
 * field that is a list, but the srcClass is the type of the elements of the list.
 * 
 * @author Ian Rae
 *
 */
public class BUListElementConverter implements ValueConverter {
	private String srcFieldName;
	private Class<?> srcElClass;
	private Class<?> destElClass;
	private boolean useScalarCopy;
	private int depth;
	private Class<?> beanClass;
	private boolean sourceIsArray;
	private List<FieldPair> fieldPairs;
	
	public BUListElementConverter(Class<?> beanClass, String fieldName, Class<?> srcElementClass, Class<?> destElementClass,
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
		
		@SuppressWarnings("unchecked")
		List<?> list = (List<?>) getAsList(value); 
		
		if (depth == 0) {
			return copyInnerMostList(list, ctx);
		} else {
			List<Object> list2 = new ArrayList<>();
			for(Object el: list) {
				List<?> inner = (List<?>) el;
				List<?> innerCopy = copyNextLevelNestedList(inner, ctx, 0);
				list2.add(innerCopy);
			}
			return list2;
		}
	}
	
	private List<?> getAsList(Object value) {
		if (sourceIsArray) {
			List<Object> list = new ArrayList<>();
			int n = Array.getLength(value);
			for(int i = 0; i < n; i++) {
				Object el = Array.get(value, i);
				list.add(el);
			}
			return list;
		} else {
			return (List<?>) value;
		}
	}

	private List<?> copyNextLevelNestedList(List<?> list, ConverterContext ctx, int currentDepth) {
		boolean isInnermost = (currentDepth == depth - 1);
		if (isInnermost) {
			return copyInnerMostList(list, ctx);
		} else {
			List<Object> list2 = new ArrayList<>();
			for(Object el: list) {
				List<?> inner = (List<?>) el;
				List<?> innerCopy = copyNextLevelNestedList(inner, ctx, currentDepth + 1);
				list2.add(innerCopy);
			}
			return list2;
		}
	}

	private List<?> copyInnerMostList(List<?> list, ConverterContext ctx) {
		if (useScalarCopy) {
			return copyScalarList(list, srcElClass);
		}

		CopySpec spec = new CopySpec();
		spec.fieldPairs = fieldPairs;
		spec.options = ctx.copyOptions;
		spec.mappingL = ctx.mappingL;
		spec.converterL = new ThreadSafeList<>();
		spec.converterL.addAll(ctx.converterL);
		spec.runawayCounter = ctx.runawayCounter;

		//list elements may be different classes (eg. List<Shape> and Circle, Square, ...)
		List<Object> list2 = new ArrayList<>();
		Class<?> prevSrcClass = null;
		for(Object el: list) {
			spec.sourceObj = el;
			spec.destObj = createObject(destElClass);
			ctx.copySvc.copyFields(spec);
			
			if (prevSrcClass == null || prevSrcClass.equals(el.getClass())) {
			} else {
				spec.executionPlanCacheKey = null; //clear so re-calc key on each element
			}
			prevSrcClass = el.getClass();
			
			list2.add(spec.destObj);
		}
		return list2;
	}

	private List<?> copyScalarList(List<?> list, Class<?> srcElClass) {
		List<Object> list2 = new ArrayList<>();
		for(Object el: list) {
			Object result = ConvertUtils.convert(el, destElClass);
			list2.add(result);
		}
		return list2;
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

	public boolean isSourceIsArray() {
		return sourceIsArray;
	}

	public void setSourceIsArray(boolean sourceIsArray) {
		this.sourceIsArray = sourceIsArray;
	}

}