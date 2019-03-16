package org.dnal.fieldcopy.service.beanutils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.ListElementConverter;
import org.dnal.fieldcopy.converter.ListElementConverterFactory;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.log.SimpleLogger;

public class FastBeanUtilFieldCopyService {
	private SimpleLogger logger;
	private BeanUtilsBean beanUtil;
	private PropertyUtilsBean propertyUtils;
	private FieldFilter fieldFilter;
	private ListElementConverterFactory converterFactory;
	
	public FastBeanUtilFieldCopyService(SimpleLogger logger, FieldFilter fieldFilter) {
		this.logger = logger;
		this.beanUtil =  BeanUtilsBean.getInstance();
		this.propertyUtils =  new PropertyUtilsBean();
		this.fieldFilter = fieldFilter;
		this.converterFactory = new ListElementConverterFactory();
	}

	public ExecuteCopyPlan generateExecutePlan(CopySpec copySpec)  {
		ExecuteCopyPlan result = null;
		try {
			result = doGenerateExecutePlan(copySpec, 1);
		} catch (Exception e) {
			throw new FieldCopyException(e.getMessage());
		}
		return result;
	}
	
	private ExecuteCopyPlan doGenerateExecutePlan(CopySpec copySpec, int runawayCounter) throws Exception {
		ExecuteCopyPlan execspec = new ExecuteCopyPlan();
		
		Object sourceObj = copySpec.sourceObj;
		Object destObj = copySpec.destObj;
		List<FieldPair> fieldPairs = copySpec.fieldPairs;
		
		if (copySpec.mappingL == null) {
			copySpec.mappingL = new ArrayList<>();
		}
		List<FieldCopyMapping> mappingL = copySpec.mappingL;
		CopyOptions options = copySpec.options;
		
		if (sourceObj == null) {
			String error = String.format("copyFields. NULL passed to sourceObj");
			throw new FieldCopyException(error);
		}
		if (destObj == null) {
			String error = String.format("copyFields. NULL passed to destObj.");
			throw new FieldCopyException(error);
		}
		if (runawayCounter > options.maxRecursionDepth) {
			String error = String.format("maxRecursionDepth exceeded. There may be a circular reference.");
			throw new FieldCopyException(error);
		}
		
		Object orig = sourceObj;
		Object dest = destObj;
		
		for(FieldPair pair: fieldPairs) {
            final FieldDescriptor origDescriptor = pair.srcProp;
            final String name = origDescriptor.getName();
            if (propertyUtils.isReadable(orig, name) &&
            		propertyUtils.isWriteable(dest, pair.destFieldName)) {
            	try {
            		fillInDestPropIfNeeded(pair, destObj.getClass());
            		
            		addListConverterIfNeeded(pair, copySpec, destObj);
            		
            		//a mapping is an explicit set of instructions for copying sub-objects (i.e. sub-beans)
            		FieldCopyMapping mapping = generateMapping(pair, mappingL);
            		if (mapping != null) {
            			FieldPlan fspec = new FieldPlan();
            			fspec.pair = pair;
            			fspec.mapping = mapping;
            			execspec.fieldL.add(fspec);
            		} else {
            			validateIsAllowed(pair);
            			
            			FieldPlan fspec = new FieldPlan();
            			fspec.pair = pair;
            			fspec.converter = convertIfPresent(pair, orig, copySpec.converterL);
            			execspec.fieldL.add(fspec);
            		}
            		
            	} catch (final NoSuchMethodException e) {
            		// Should not happen
            	}
            }
		}
		return execspec;
	}
	private void fillInDestPropIfNeeded(FieldPair pair, Class<? extends Object> class2) {
		if (pair.destProp != null) {
			return;
		}
        final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(class2);
		
        for (int i = 0; i < arDest.length; i++) {
        	PropertyDescriptor pd = arDest[i];
        	if (! fieldFilter.shouldProcess(class2, pd.getName())) {
        		continue; // No point in trying to set an object's class
            }

        	if (pd.getName().equals(pair.destFieldName)) {
        		pair.destProp = new BeanUtilsFieldDescriptor(pd);
        		return;
        	}
        }
	}

	
	private void addListConverterIfNeeded(FieldPair pair, CopySpec copySpec, Object destObj) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (Collection.class.isAssignableFrom(srcFieldClass) && 
				Collection.class.isAssignableFrom(srcFieldClass)) {
			
			if (copySpec.converterL == null) {
				copySpec.converterL = new ArrayList<>();
			}

			for(ValueConverter converter: copySpec.converterL) {
				if (converter.canConvert(pair.srcProp.getName(), srcFieldClass, destFieldClass)) {
					//if already is a converter, nothing more to do
					return;
				}
			}
			
			if (ReflectionUtil.elementIsList(copySpec.sourceObj, fd1)) {
				//TODO: also check destObj??
				return;
			}

			//add one
			String name = pair.srcProp.getName();
			Class<?> srcElementClass = ReflectionUtil.detectElementClass(copySpec.sourceObj, fd1);
			Class<?> destElementClass = ReflectionUtil.detectElementClass(destObj, fd2);
			ListElementConverter converter = converterFactory.createConverter(name, srcElementClass, destElementClass);
			if (converter == null) {
				String error = String.format("Copying list<%s> to list<%s> is not supported.", srcElementClass.getName(), destElementClass.getName());
				throw new FieldCopyException(error);
			}
			
			copySpec.converterL.add(converter);
		}
	}

	private ValueConverter convertIfPresent(FieldPair pair, Object orig, List<ValueConverter> converterL) {
		if (CollectionUtils.isNotEmpty(converterL)) {
			BeanUtilsFieldDescriptor desc = (BeanUtilsFieldDescriptor) pair.destProp;
			Class<?> destClass = desc.pd.getPropertyType();
			
			BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
			Class<?> srcClass = fd1.pd.getPropertyType();
			//TODO: can we make this faster with a map??
			for(ValueConverter converter: converterL) {
				//TODO: fix value null issue
				if (converter.canConvert(pair.srcProp.getName(), srcClass, destClass)) {
					return converter;
				}
			}
		}
		return null;
	}

	private void validateIsAllowed(FieldPair pair) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
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
			if (Collection.class.isAssignableFrom(type)) {
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

	private FieldCopyMapping generateMapping(FieldPair pair, List<FieldCopyMapping> mappingL) throws Exception {
		BeanUtilsFieldDescriptor fd = (BeanUtilsFieldDescriptor) pair.srcProp;
		for(FieldCopyMapping mapping: mappingL) {
			if (mapping.getClazzSrc().equals(fd.pd.getPropertyType())) {
				if (pair.destProp == null) {
					throw new IllegalArgumentException("fix later");
				}
				BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
				if (mapping.getClazzDest().equals(fd2.pd.getPropertyType())) {
					return mapping;
				}
			}
		}
		return null;
	}
	private boolean applyMapping(FieldCopyService outerSvc, CopySpec copySpec,  
			FieldPair pair, Object sourceObj, Object destObj, Object srcValue, FieldCopyMapping mapping, int runawayCounter) throws Exception {
		if (srcValue == null) {
			return true;
		}

		//use the mapping, which has fields, autocopy flag etc
		Object destValue = propertyUtils.getSimpleProperty(destObj, pair.destFieldName);
		if (destValue == null) {
			destValue = createObject(mapping.getClazzDest());
			beanUtil.copyProperty(destObj, pair.destFieldName, destValue);
		}

		//**recursion**
		CopySpec spec = new CopySpec();
		spec.sourceObj = srcValue;
		spec.destObj = destValue;
		spec.fieldPairs = mapping.getFieldPairs();
		spec.mappingL = copySpec.mappingL;
		spec.options = copySpec.options;
		
		BeanUtilsFieldCopyService altSvc = (BeanUtilsFieldCopyService) outerSvc;
		altSvc.doCopyFields(spec, runawayCounter + 1);
//		applyMapping(copySpec, pair, srcValue, destValue, srcValue, mapping, runawayCounter + 1);

		return true;
	}
	
	private Object createObject(Class<?> clazzDest) throws InstantiationException, IllegalAccessException {
		return clazzDest.newInstance();
	}
	
	public boolean executePlan(CopySpec spec, ExecuteCopyPlan execPlan, FieldCopyService outerSvc, int runawayCounter)  {
		boolean b = false;
		try {
			b = doExecutePlan(spec, execPlan, outerSvc, runawayCounter);
		} catch (Exception e) {
			throw new FieldCopyException(e.getMessage());
		}
		return b;
	}

	private boolean doExecutePlan(CopySpec spec, ExecuteCopyPlan execPlan, FieldCopyService outerSvc, int runawayCounter) throws Exception {
		boolean ok = true;
		for(FieldPlan fieldPlan: execPlan.fieldL) {
			String name = fieldPlan.pair.srcProp.getName();
    		Object value = propertyUtils.getSimpleProperty(spec.sourceObj, name);
			if (fieldPlan.converter != null) {
				BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) fieldPlan.pair.destProp;
				Class<?> destClass = fd2.pd.getPropertyType();

				BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) fieldPlan.pair.srcProp;
				Class<?> srcClass = fd1.pd.getPropertyType();
				
				ConverterContext ctx = new ConverterContext();
				ctx.destClass = destClass;
				ctx.srcClass = srcClass;
				ctx.srcFieldName = name;
				ctx.copySvc = outerSvc;
				ctx.copyOptions = spec.options;
				value = fieldPlan.converter.convertValue(spec.sourceObj, value, ctx);
			}
			
			if (fieldPlan.mapping != null) {
				applyMapping(outerSvc, spec, fieldPlan.pair, spec.sourceObj, spec.destObj, value, fieldPlan.mapping, runawayCounter);
			} else {
				beanUtil.copyProperty(spec.destObj, fieldPlan.pair.destFieldName, value);
			}
		}
		return ok;
	}
	
	
}