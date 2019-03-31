package org.dnal.fieldcopy.service.beanutils.old;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
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
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BUBeanDetectorService;

public class FastBeanUtilFieldCopyService {
	private SimpleLogger logger;
	private BeanUtilsBean beanUtil;
	private PropertyUtilsBean propertyUtils;
	private FieldFilter fieldFilter;
	private ConverterService converterSvc;
	private BUBeanDetectorService beanDetectorSvc;
	
	public FastBeanUtilFieldCopyService(SimpleLogger logger, FieldFilter fieldFilter) {
		this.logger = logger;
		this.beanUtil =  BeanUtilsBean.getInstance();
		this.propertyUtils =  new PropertyUtilsBean();
		this.fieldFilter = fieldFilter;
		this.beanDetectorSvc = new BUBeanDetectorService();
		this.converterSvc = new ConverterService(logger, this.beanDetectorSvc);
	}

	public ExecuteCopyPlan generateExecutePlan(CopySpec copySpec, FieldCopyService outerSvc)  {
		ExecuteCopyPlan result = null;
		ExecuteCopyPlan execspec = new ExecuteCopyPlan();
		try {
			result = doGenerateExecutePlan(copySpec, outerSvc, execspec, 1);
		} catch (Exception e) {
			String s = " while generating execution plan:";
			String className = String.format("'%s'", copySpec.sourceObj.getClass().getSimpleName());
			String field = execspec.currentFieldName == null ? "" : " field '" + execspec.currentFieldName + "'";
			String msg = String.format("Exception in %s %s: %s%s %s", className, field,
					e.getClass().getSimpleName(), s, e.getMessage());
			throw new FieldCopyException(msg, e);
		}
		
		result.currentFieldName = null; //reset
		return result;
	}
	
	private ExecuteCopyPlan doGenerateExecutePlan(CopySpec copySpec, FieldCopyService outerSvc, ExecuteCopyPlan execspec, int runawayCounter) throws Exception {
		
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
            execspec.currentFieldName = name;
            
            if (propertyUtils.isReadable(orig, name) &&
            		propertyUtils.isWriteable(dest, pair.destFieldName)) {
            	try {
            		fillInDestPropIfNeeded(pair, destObj.getClass());
            		
            		converterSvc.addListConverterIfNeeded(pair, copySpec, destObj.getClass());
            		converterSvc.addArrayListConverterIfNeeded(pair, copySpec, destObj.getClass());
            		converterSvc.addArrayConverterIfNeeded(pair, copySpec, destObj.getClass());
            		converterSvc.addListArrayConverterIfNeeded(pair, copySpec, destObj.getClass());
            		
            		//a mapping is an explicit set of instructions for copying sub-objects (i.e. sub-beans)
            		FieldCopyMapping mapping = findOrGenerateMapping(pair, mappingL, outerSvc, copySpec);
            		if (mapping != null) {
            			FieldPlan fspec = new FieldPlan();
            			fspec.pair = pair;
            			fspec.mapping = mapping;
            			execspec.fieldL.add(fspec);
            		} else {
            			validateIsAllowed(pair);
            			
            			FieldPlan fspec = new FieldPlan();
            			fspec.pair = pair;
            			fspec.converter = converterSvc.findConverter(copySpec, pair, orig, copySpec.converterL);
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

	private FieldCopyMapping findOrGenerateMapping(FieldPair pair, List<FieldCopyMapping> mappingL, FieldCopyService outerSvc, CopySpec copySpec) throws Exception {
		FieldCopyMapping mapping = findMapping(pair, mappingL, outerSvc, copySpec);
		if (mapping != null) {
			return mapping;
		}
		
		//auto-generate mappings for sub-objects
		//-first, detect that we are in a sub-obj (not in main obj)
		//-create mapping for src,dest (so that sub-obj gets converters, etc)
		mapping = autoGenerateMapping(pair, outerSvc, copySpec);
		if (mapping != null) {
			mappingL.add(mapping);
		}
		return mapping;
	}
	private FieldCopyMapping findMapping(FieldPair pair, List<FieldCopyMapping> mappingL, FieldCopyService outerSvc, CopySpec copySpec) throws Exception {
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
	private FieldCopyMapping autoGenerateMapping(FieldPair pair, FieldCopyService outerSvc, CopySpec copySpecParam) {
		BeanUtilsFieldDescriptor fd = (BeanUtilsFieldDescriptor) pair.srcProp;
		Class<?> srcClass = fd.pd.getPropertyType();
		if (!beanDetectorSvc.isBeanClass(srcClass)) {
			return null;
		}
		
		BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
		Class<?> destClass = fd2.pd.getPropertyType();
		
		FieldCopyMapping mapping = null;
		try {
			mapping = doAutoGenMapping(pair, outerSvc, copySpecParam, srcClass, destClass);
		} catch (Exception e) {
			String err = String.format("Failed during auto-generate sub-mapping %s to %s", srcClass.getName(), destClass.getName());
			throw new FieldCopyException(err);
		}
		return mapping;
	}

	private FieldCopyMapping doAutoGenMapping(FieldPair zpair, FieldCopyService outerSvc, CopySpec copySpecParam, Class<?> srcClass, Class<?> destClass) throws Exception {
		XBeanUtilsFieldCopyService bufc = (XBeanUtilsFieldCopyService) outerSvc;
		List<FieldPair> fieldPairs = bufc.buildAutoCopyPairsNoRegister(srcClass, destClass);
		
		CopySpec innerSpec = new CopySpec();
		innerSpec.converterL = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(copySpecParam.converterL)) {
			innerSpec.converterL.addAll(copySpecParam.converterL);
		}
		innerSpec.destObj = null; //!!
		innerSpec.fieldPairs = fieldPairs;
		innerSpec.mappingL = new ArrayList<>();
		innerSpec.options = copySpecParam.options;
		
		//this may throw exceptions
		innerSpec.sourceObj = propertyUtils.getSimpleProperty(copySpecParam.sourceObj, zpair.srcProp.getName());
		if (innerSpec.sourceObj == null) {
			return null;
		}
		
		//inspect each field of the sub-object. If it needs a converter or mapping
		//then create a mapping. If not, then BeanUtils will do a simple copy.
		//Wait a sec! Since we only inspect direct sub-obj, we don't know if their sub-sub-objs
		//might use a mapping or converter.
		//so set needMapping to true unless we know there are no converters or mappings...
		boolean needMapping = true;
		Object orig = innerSpec.sourceObj;
		for(FieldPair pair: fieldPairs) {
            final FieldDescriptor origDescriptor = pair.srcProp;
            final String name = origDescriptor.getName();
            
            if (propertyUtils.isReadable(orig, name)) {
// TODO           		propertyUtils.isWriteable(destObj, pair.destFieldName)) {
            	try {
            		fillInDestPropIfNeeded(pair, destClass.getClass());
            		
            		int numConverters = innerSpec.converterL.size();
            		converterSvc.addListConverterIfNeeded(pair, innerSpec, destClass);
            		converterSvc.addArrayListConverterIfNeeded(pair, innerSpec, destClass);
            		converterSvc.addArrayConverterIfNeeded(pair, innerSpec, destClass);
            		converterSvc.addListArrayConverterIfNeeded(pair, innerSpec, destClass);
            		if (numConverters != innerSpec.converterL.size()) {
            			needMapping = true;
            		}
            		
            		//a mapping is an explicit set of instructions for copying sub-objects (i.e. sub-beans)
					FieldCopyMapping mapping = findMapping(pair, copySpecParam.mappingL, outerSvc, innerSpec);
            		if (mapping != null) {
            			needMapping = true;
            		} else {
            			validateIsAllowed(pair);
            			ValueConverter conv = converterSvc.findConverter(innerSpec, pair, orig, innerSpec.converterL);
            			if (conv != null) {
            				needMapping = true;
            			}
            		}
            	} catch (final NoSuchMethodException e) {
            		// Should not happen
            	}
            }
		}
		
		if (needMapping) {
			FieldCopyMapping mapping = new FieldCopyMapping(srcClass, destClass, fieldPairs);
			return mapping;
		}
		
		return null;
	}

	private boolean applyMapping(FieldCopyService outerSvc, CopySpec copySpec,  
			FieldPair pair, Object sourceObj, Object destObj, Object srcValue, 
			FieldCopyMapping mapping, int runawayCounter) throws Exception {
		
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
		spec.converterL = copySpec.converterL;
		//important that we directly pass mappingL and converterL so that
		//any mappings or converters created get added to copySpec
		
		XBeanUtilsFieldCopyService altSvc = (XBeanUtilsFieldCopyService) outerSvc;
		altSvc.doCopyFields(spec, runawayCounter + 1);

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
			String s = execPlan.inConverter ? " in converter:" : ":";
			String className = String.format("'%s'", spec.sourceObj.getClass().getSimpleName());
			String field = execPlan.currentFieldName == null ? "" : " field '" + execPlan.currentFieldName + "'";
			String msg = String.format("Exception in %s %s: %s%s %s", className, field,
					e.getClass().getSimpleName(), s, e.getMessage());
			throw new FieldCopyException(msg, e);
		}
		return b;
	}

	private boolean doExecutePlan(CopySpec spec, ExecuteCopyPlan execPlan, FieldCopyService outerSvc, int runawayCounter) throws Exception {
		boolean ok = true;
		for(FieldPlan fieldPlan: execPlan.fieldL) {
			String name = fieldPlan.pair.srcProp.getName();
			execPlan.currentFieldName = name;
    		Object value = propertyUtils.getSimpleProperty(spec.sourceObj, name);
    		
    		logger.log("  field %s: %s", name, getLoggableString(value));
    		
			if (fieldPlan.converter != null) {
				BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) fieldPlan.pair.destProp;
				Class<?> destClass = fd2.pd.getPropertyType();

				BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) fieldPlan.pair.srcProp;
				Class<?> srcClass = fd1.pd.getPropertyType();
				
				ConverterContext ctx = new ConverterContext();
				ctx.destClass = destClass;
				ctx.srcClass = srcClass;
				ctx.copySvc = outerSvc;
				ctx.copyOptions = spec.options;
				ctx.beanDetectorSvc = this.beanDetectorSvc;
				addConverterAndMappingLists(ctx, spec);
				execPlan.inConverter = true;
				value = fieldPlan.converter.convertValue(spec.sourceObj, value, ctx);
				execPlan.inConverter = false;
			}
			
			if (value == null) {
				value = fieldPlan.pair.defaultValue;
			}
			
			if (fieldPlan.mapping != null) {
				applyMapping(outerSvc, spec, fieldPlan.pair, spec.sourceObj, spec.destObj, value, fieldPlan.mapping, runawayCounter);
			} else {
				beanUtil.copyProperty(spec.destObj, fieldPlan.pair.destFieldName, value);
			}
		}
		return ok;
	}

	private void addConverterAndMappingLists(ConverterContext ctx, CopySpec spec) {
		if (CollectionUtils.isNotEmpty(spec.mappingL)) {
			ctx.mappingL = new ArrayList<>();
			ctx.mappingL.addAll(spec.mappingL);
		}
		if (CollectionUtils.isNotEmpty(spec.converterL)) {
			ctx.converterL = new ArrayList<>();
			ctx.converterL.addAll(spec.converterL);
		}
	}

	private Object getLoggableString(Object value) {
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

	public ConverterService getConverterSvc() {
		return converterSvc;
	}
	
	
}