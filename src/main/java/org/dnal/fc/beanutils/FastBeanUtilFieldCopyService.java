package org.dnal.fc.beanutils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.dnal.fc.CopyOptions;
import org.dnal.fc.FieldCopyMapping;
import org.dnal.fc.core.CopySpec;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldDescriptor;
import org.dnal.fc.core.FieldFilter;
import org.dnal.fc.core.FieldPair;
import org.dnal.fc.core.ListElementTransformer;
import org.dnal.fc.core.ValueTransformer;
import org.dnal.fieldcopy.FieldCopyException;
import org.dnal.fieldcopy.log.SimpleLogger;

public class FastBeanUtilFieldCopyService {
	private SimpleLogger logger;
	private BeanUtilsBean beanUtil;
	private PropertyUtilsBean propertyUtils;
	private FieldFilter fieldFilter;
	
	public FastBeanUtilFieldCopyService(SimpleLogger logger, FieldFilter fieldFilter) {
		this.logger = logger;
		this.beanUtil =  BeanUtilsBean.getInstance();
		this.propertyUtils =  new PropertyUtilsBean();
		this.fieldFilter = fieldFilter;
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

	public ExecuteCopySpec generateExecutePlan(CopySpec copySpec)  {
		ExecuteCopySpec result = null;
		try {
			result = doGenerateExecutePlan(copySpec, 1);
		} catch (Exception e) {
			throw new FieldCopyException(e.getMessage());
		}
		return result;
	}
	
	private ExecuteCopySpec doGenerateExecutePlan(CopySpec copySpec, int runawayCounter) throws Exception {
		ExecuteCopySpec execspec = new ExecuteCopySpec();
		
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
            if ("class".equals(name)) {
            	continue; // No point in trying to set an object's class
            }
            if (propertyUtils.isReadable(orig, name) &&
            		propertyUtils.isWriteable(dest, pair.destFieldName)) {
            	try {
            		fillInDestPropIfNeeded(pair, destObj.getClass());
            		
            		addListTransformerIfNeeded(pair, copySpec.transformerL, destObj);
            		
            		FieldCopyMapping mapping = generateMapping(pair, mappingL);
            		if (mapping != null) {
            			ExecuteFieldSpec fspec = new ExecuteFieldSpec();
            			fspec.pair = pair;
            			fspec.mapping = mapping;
            			execspec.fieldL.add(fspec);
            		} else {
            			validateIsAllowed(pair);
            			
            			ExecuteFieldSpec fspec = new ExecuteFieldSpec();
            			fspec.pair = pair;
            			fspec.transformer = transformIfPresent(pair, orig, copySpec.transformerL);
            			execspec.fieldL.add(fspec);
            		}
            		
            	} catch (final NoSuchMethodException e) {
            		// Should not happen
            	}
            }
		}
		return execspec;
	}
	
	private void addListTransformerIfNeeded(FieldPair pair, List<ValueTransformer> transformerL, Object destObj) {
		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		
		Class<?> srcFieldClass = fd1.pd.getPropertyType();
		Class<?> destFieldClass = fd2.pd.getPropertyType();
		if (Collection.class.isAssignableFrom(srcFieldClass) && 
				Collection.class.isAssignableFrom(srcFieldClass)) {
			
			if (transformerL == null) {
				transformerL = new ArrayList<>();
			}

			for(ValueTransformer transformer: transformerL) {
				//TODO: is it ok to pass null for value
				if (transformer.canHandle(pair.srcProp.getName(), null, destFieldClass)) {
					//if already is a transform, nothing more to do
					return;
				}
			}

			//add one
			String name = pair.srcProp.getName();
			Class<?> destElementClass = ReflectionUtil.detectElementClass(destObj, fd2);
			ListElementTransformer transformer = new ListElementTransformer(name, destElementClass);
			transformerL.add(transformer);
		}
	}

	private ValueTransformer transformIfPresent(FieldPair pair, Object orig, List<ValueTransformer> transformerL) {
		if (CollectionUtils.isNotEmpty(transformerL)) {
			BeanUtilsFieldDescriptor desc = (BeanUtilsFieldDescriptor) pair.destProp;
			Class<?> destClass = desc.pd.getPropertyType();
			//TODO: can we make this faster with a map??
			for(ValueTransformer transformer: transformerL) {
				//TODO: fix value null issue
				if (transformer.canHandle(pair.srcProp.getName(), null, destClass)) {
					return  transformer;
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
		
		if (Number.class.isAssignableFrom(srcType)) {
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
	private boolean applyMapping(FieldCopyService outerSvc, CopySpec copySpec, FieldPair pair, Object sourceObj, Object destObj, Object srcValue, FieldCopyMapping mapping, int runawayCounter) throws Exception {
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
		
		AlternateFieldCopyService altSvc = (AlternateFieldCopyService) outerSvc;
		altSvc.doCopyFields(spec, runawayCounter + 1);
//		applyMapping(copySpec, pair, srcValue, destValue, srcValue, mapping, runawayCounter + 1);

		return true;
	}
	
	private Object createObject(Class<?> clazzDest) throws InstantiationException, IllegalAccessException {
		return clazzDest.newInstance();
	}
	
	public boolean executePlan(CopySpec spec, ExecuteCopySpec execSpec, FieldCopyService outerSvc, int runawayCounter)  {
		boolean b = false;
		try {
			b = doExecutePlan(spec, execSpec, outerSvc, runawayCounter);
		} catch (Exception e) {
			throw new FieldCopyException(e.getMessage());
		}
		return b;
	}

	private boolean doExecutePlan(CopySpec spec, ExecuteCopySpec execSpec, FieldCopyService outerSvc, int runawayCounter) throws Exception {
		boolean ok = true;
		for(ExecuteFieldSpec fieldPlan: execSpec.fieldL) {
			String name = fieldPlan.pair.srcProp.getName();
    		Object value = propertyUtils.getSimpleProperty(spec.sourceObj, name);
			if (fieldPlan.transformer != null) {
				BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) fieldPlan.pair.destProp;
				Class<?> destClass = fd2.pd.getPropertyType();
				value = fieldPlan.transformer.transformValue(name, spec.sourceObj, value, destClass);
			}
			
			if (fieldPlan.mapping != null) {
				applyMapping(outerSvc, spec, fieldPlan.pair, spec.sourceObj, spec.destObj, value, fieldPlan.mapping, runawayCounter);
			}
			beanUtil.copyProperty(spec.destObj, fieldPlan.pair.destFieldName, value);
		}
		return ok;
	}
	
	
}