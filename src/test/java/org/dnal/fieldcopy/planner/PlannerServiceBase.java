package org.dnal.fieldcopy.planner;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsBeanDetectorService;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;

public abstract class PlannerServiceBase implements FieldCopyService {
	
	protected SimpleLogger logger;
	protected FieldRegistry registry;
	protected BeanUtilsBean beanUtil;
	protected PropertyUtilsBean propertyUtils;
	protected FieldFilter fieldFilter;
	protected BeanUtilsBeanDetectorService beanDetectorSvc;

	public PlannerServiceBase(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
		this.logger = logger;
		this.registry = registry;
		this.beanUtil =  BeanUtilsBean.getInstance();
		this.propertyUtils =  new PropertyUtilsBean();
		this.fieldFilter = fieldFilter;
		this.beanDetectorSvc = new BeanUtilsBeanDetectorService();
		
		//customize Date converter.  There is no good defaut for date conversions
		//so well just do yyyy-MM-dd
		DateConverter dateConverter = new DateConverter();
		dateConverter.setPattern("yyyy-MM-dd");
		ConvertUtils.register(dateConverter, Date.class);
	}
	

	@Override
	public List<FieldPair> buildAutoCopyPairs(Class<? extends Object> class1, Class<? extends Object> class2) {
        List<FieldPair> fieldPairs = registry.findAutoCopyInfo(class1, class2);
		if (fieldPairs != null) {
			return fieldPairs;
		}
		
        fieldPairs = buildAutoCopyPairsNoRegister(class1, class2);
		registry.registerAutoCopyInfo(class1, class2, fieldPairs);
        return fieldPairs;
	}
	
	public List<FieldPair> buildAutoCopyPairsNoRegister(Class<? extends Object> class1, Class<? extends Object> class2) {
        final PropertyDescriptor[] arSrc = propertyUtils.getPropertyDescriptors(class1);
        final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(class2);
		
        List<FieldPair> fieldPairs = new ArrayList<>();
        for (int i = 0; i < arSrc.length; i++) {
        	PropertyDescriptor pd = arSrc[i];
        	if (! fieldFilter.shouldProcess(class1, pd.getName())) {
        		continue; // No point in trying to set an object's class
            }

        	PropertyDescriptor targetPd = findMatchingField(arDest, pd.getName());
        	
        	FieldPair pair = new FieldPair();
        	pair.srcProp = new BeanUtilsFieldDescriptor(pd);
        	pair.destFieldName = (targetPd == null) ? null : targetPd.getName();
        	pair.destProp = new BeanUtilsFieldDescriptor(targetPd);
        	fieldPairs.add(pair);
        }
        return fieldPairs;
	}
	
	private PropertyDescriptor findMatchingField(PropertyDescriptor[] arDest, String name) {
		for (int i = 0; i < arDest.length; i++) {
			
			PropertyDescriptor pd = arDest[i];
			if (pd.getName().equals(name)) {
				return pd;
			}
		}
		return null;
	}

	protected void validateIsAllowed(FieldPair pair) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
		Class<?> destClass = isNotAllowed(pair);
		if (destClass != null) {
			BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
			String err = String.format("Not allowed to copy %s to %s", fd1.pd.getPropertyType().getName(), destClass.getName());
			throw new FieldCopyException(err);
		}
	}

	/**
	 * Additional rules that we want to enfore.
	 * For example, not allowed to copy an int to a boolean.
	 * 
	 * @return null if allowed, else class that we are NOT allowed to copy to
	 */
	private Class<?> isNotAllowed(FieldPair pair) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		Class<?> srcType = fd1.pd.getPropertyType();
		BeanUtilsFieldDescriptor desc = (BeanUtilsFieldDescriptor) pair.destProp;
		Class<?> type = desc.pd.getPropertyType();
		
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
	
	@Override
	public void dumpFields(Object sourceObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public SimpleLogger getLogger() {
		return logger;
	}

	@Override
	public FieldRegistry getRegistry() {
		return registry;
	}

	@Override
	public void addBuiltInConverter(ValueConverter converter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String generateExecutionPlanCacheKey(CopySpec spec) {
		// TODO Auto-generated method stub
		return null;
	}
	
}