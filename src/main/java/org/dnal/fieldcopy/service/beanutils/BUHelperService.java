package org.dnal.fieldcopy.service.beanutils;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.log.SimpleLogger;

public class BUHelperService {
	
	protected SimpleLogger logger;
	protected BeanUtilsBean beanUtil;

	public BUHelperService(SimpleLogger logger) {
		this.logger = logger;
		this.beanUtil =  BeanUtilsBean.getInstance();
	}

	public void validateIsAllowed(FieldPair pair) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
		Class<?> destClass = isNotAllowed(pair);
		if (destClass != null) {
			BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp; //TODO: support SourceValueDescriptor
			String err = String.format("Not allowed to copy %s to %s", fd1.pd.getPropertyType().getName(), destClass.getName());
			throw new FieldCopyException(err);
		}
	}

	/**
	 * Additional rules that we want to enforce.
	 * For example, not allowed to copy an int to a boolean.
	 * 
	 * @return null if allowed, else class that we are NOT allowed to copy to
	 */
	private Class<?> isNotAllowed(FieldPair pair) {
		Class<?> srcType = pair.getSrcClass();
		Class<?> type = pair.getDestClass();
		
		if (Number.class.isAssignableFrom(srcType) || isNumberPrimitive(srcType)) {
			if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {			
				return type;
			} else if (Date.class.equals(type)) {
				if (Long.class.equals(srcType) || Long.TYPE.equals(srcType)) {
				} else {
					return type;
				}
			}
		} else if (Date.class.isAssignableFrom(srcType)) {
			if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {			
				return type;
			} else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {			
				return type;
			} else if (Double.class.equals(type) || Double.TYPE.equals(type)) {			
				return type;
			} else if (type.isEnum()) {			
				return type;
			}
		} else if (srcType.isEnum()) {
			if (type.isEnum()) {
			} else if (String.class.equals(type)) {
			} else {
				return type;
			}
		} else if (Collection.class.isAssignableFrom(srcType)) {
			if (Collection.class.isAssignableFrom(type) || type.isArray()) {
			} else {
				return type;
			}
		} else if (srcType.isArray()) {
			if (type.isArray() || Collection.class.isAssignableFrom(type)) {
			} else {
				return type;
			}
		}
		return null;
	}
	private boolean isNumberPrimitive(Class<?> srcType) {
		if (Integer.TYPE.equals(srcType) ||
				Long.TYPE.equals(srcType) ||
				Double.TYPE.equals(srcType) ||
				Float.TYPE.equals(srcType) ||
				Short.TYPE.equals(srcType)) {
			return true;
		}
		return false;
	}
	
	public void dumpFields(Object sourceObj) {
		try {
			Map<String,String> map = beanUtil.describe(sourceObj);
			for(String fieldName: map.keySet()) {
				String val = map.get(fieldName);
				logger.log("%s = %s", fieldName, val);
			}
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String generateExecutionPlanCacheKey(CopySpec spec) {
		//NOTE. the following key will not work if you have multiple conversions of the
		//same pair of source,destObj but with different fields, mappings, and converters.
		//If that is the case, you MUST key cacheKey and provide a unique value.

		//if source or destObj are null we will catch it during copy
		String class1Name = spec.sourceObj == null ? "" : spec.sourceObj.getClass().getName();
		String class2Name = spec.destObj == null ? "" : spec.destObj.getClass().getName();
		return String.format("%s--%s", class1Name, class2Name);
	}
	
	public Object getLoggableString(Object value) {
		if (value == null || ! logger.isEnabled()) {
			return null;
		}
		
		if (value.getClass().isArray()) {
			int n = Array.getLength(value);
			String s = String.format("array(len=%d): ", n);
			for(int i = 0; i < n; i++) {
				Object el = Array.get(value, i);
				s += String.format("%s ", el.toString());
				if (i >= 2) {
					s += "...";
					break;
				}
			}
			return s;
		} else {
			String s = value.toString();
			if (s.length() > 100) {
				s = s.substring(0, 100);
			}
			return s;
		}
	}
}