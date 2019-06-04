package org.dnal.fieldcopy.service.beanutils;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyUtils;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.core.SourceValueFieldDescriptor;
import org.dnal.fieldcopy.core.TargetPair;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.metrics.CopyMetrics;
import org.dnal.fieldcopy.metrics.DoNothingMetrics;
import org.dnal.fieldcopy.util.ThreadSafeList;

/**
 * Apache BeanUtils implementation of FieldCopyService.
 * Generates (and caches) a copy plan.
 * 
 * @author Ian Rae
 *
 */
public class BUCopyService extends BUCopyServiceBase {
	private static class PlanCreateState {
		public String currentFieldName;
		public int runawayCounter = 1;
	}
	
	private Map<String,BUClassPlan> executionPlanMap = new ConcurrentHashMap<>();
	private boolean enablePlanCache = true;
	private BUConverterService converterSvc;
	private CopyMetrics metrics = new DoNothingMetrics();
	private BUFieldSetterService fieldSetterSvc;

	public BUCopyService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
		super(logger, registry, fieldFilter);
		this.converterSvc = new BUConverterService(logger, this.beanDetectorSvc, this);
		this.fieldSetterSvc = new BUFieldSetterService(logger);
	}
	
	public int getPlanCacheSize() {
		return executionPlanMap.size();
	}
	public BUClassPlan findPlan(String srcClassName) {
		for(String key: executionPlanMap.keySet()) {
			if (key.contains(srcClassName)) {
				return executionPlanMap.get(key);
			}
		}
		return null;
	}
	
	@Override
	public void addBuiltInConverter(ValueConverter converter) {
		this.converterSvc.addBuiltInConverter(converter);
	}
	
	@Override
	public void copyFields(CopySpec copySpec) {
		Object sourceObj = copySpec.sourceObj;
		Object destObj = copySpec.destObj;

		if (sourceObj == null) {
			String error = String.format("copyFields. NULL passed to sourceObj");
			throw new FieldCopyException(error);
		}
		if (destObj == null) {
			String error = String.format("copyFields. NULL passed to destObj.");
			throw new FieldCopyException(error);
		}
		
		BUClassPlan classPlan = getOrCreatePlan(copySpec);
		
		logStartOfCopy(copySpec, classPlan);
		BUExecutePlan execPlan = new BUExecutePlan();
		execPlan.srcObject = sourceObj;
		execPlan.destObj = destObj;
		execPlan.classPlan = classPlan;
		execPlan.copySpec = copySpec;
		//TODO anything else to propagate?
		//propogateStuff(execSpec, copySpec);

		this.executePlan(execPlan, copySpec.runawayCounter);
	}
	
	private void logStartOfCopy(CopySpec copySpec, BUClassPlan classPlan) {
		//TODO copyOptions should have flag for log full name or simple name
		String srcName = copySpec.sourceObj == null ? "" : copySpec.sourceObj.getClass().getSimpleName();
		String destName = copySpec.destObj == null ? "" : copySpec.destObj.getClass().getSimpleName();
		logger.log("COPY %s -> %s: using plan: %d fields", srcName, destName, classPlan.fieldPlanL.size());
	}

	private BUClassPlan getOrCreatePlan(CopySpec copySpec) {
		Object sourceObj = copySpec.sourceObj;
		Object destObj = copySpec.destObj;
		
		BUClassPlan classPlan = null;
		if (enablePlanCache) {
			if (copySpec.executionPlanCacheKey == null) {
				copySpec.executionPlanCacheKey = generateExecutionPlanCacheKey(copySpec);
			}
			classPlan = executionPlanMap.get(copySpec.executionPlanCacheKey);
		}
		
		if (classPlan == null) {
			PlanCreateState state = new PlanCreateState();
			try {
				classPlan = buildClassPlan(sourceObj, destObj, sourceObj.getClass(), destObj.getClass(), copySpec.fieldPairs, copySpec, state);
			} catch(FieldCopyException e) {
				throw e; //rethrow
			}catch (Exception e) {
				String s = " while generating execution plan:";
				String className = String.format("'%s'", copySpec.sourceObj.getClass().getSimpleName());
				String field = state.currentFieldName == null ? "" : " field '" + state.currentFieldName + "'";
				String msg = String.format("Exception in %s %s: %s%s %s", className, field,
						e.getClass().getSimpleName(), s, e.getMessage());
				throw new FieldCopyException(msg, e);
			}
			
			if (enablePlanCache) {
				executionPlanMap.put(copySpec.executionPlanCacheKey, classPlan);
				metrics.incrementPlanCount();
			}
		} else if (copySpec.hasSourceValueMap()) { 
			classPlan = cloneClassPlanForSourceValueFDs(classPlan, copySpec);
		}
		
		return classPlan;
	}

	/**
	 * To be thread-safe we can't update a cached plan, so clone and update the clone.
	 * @param param
	 * @param copySpec
	 * @return copy of param
	 */
	private BUClassPlan cloneClassPlanForSourceValueFDs(BUClassPlan param, CopySpec copySpec) {
		BUClassPlan classPlan = new BUClassPlan();
		classPlan.converterL = param.converterL;
		classPlan.destClass = param.destClass;
		classPlan.srcClass = param.srcClass;

		List<BUFieldPlan> list = new ArrayList<>();
		Iterator<BUFieldPlan> iter = param.fieldPlanL.iterator();
		while(iter.hasNext()) {
			BUFieldPlan fieldPlan = iter.next();
			if (fieldPlan.srcFd instanceof SourceValueFieldDescriptor) {
				BUFieldPlan clone = fieldPlan.clone();
				SourceValueFieldDescriptor existing = (SourceValueFieldDescriptor) fieldPlan.srcFd;
				String key = existing.getName();
				//TODO: throw error if map doesn't have non-null value for key
				clone.srcFd = new SourceValueFieldDescriptor(key, copySpec.additionalSourceValMap.get(key));
				list.add(clone);
			} else {
				list.add(fieldPlan);
			}
		}
		classPlan.fieldPlanL = new ThreadSafeList<>();
		classPlan.fieldPlanL.addAll(list);
		return classPlan;
	}

	private BUClassPlan buildClassPlan(Object srcObj, Object destObj, Class<?> srcClass, Class<?> destClass, List<FieldPair> fieldPairs, CopySpec copySpec, PlanCreateState state) throws Exception {
		if (srcObj == null) {
			String error = String.format("buildClassPlan. srcObj is NULL");
			throw new FieldCopyException(error);
		}
		//destObj can be null
		
		if (state.runawayCounter > copySpec.options.maxRecursionDepth) {
			String error = String.format("maxRecursionDepth exceeded during plan creation. There may be a circular reference.");
			throw new FieldCopyException(error);
		}
		
		BUClassPlan classPlan = new BUClassPlan();
		classPlan.srcClass = srcClass;
		classPlan.destClass = destClass;
		if (copySpec.converterL != null) {
			classPlan.converterL.addAll(copySpec.converterL);
		}

		List<BUFieldPlan> tmpL = new ArrayList<>();
		for(FieldPair pair: fieldPairs) {
			final FieldDescriptor origDescriptor = pair.srcProp;
			final String name = origDescriptor.getName();
			state.currentFieldName = name; //for logging errors
			
			//check for readability and writability
			boolean isSourceValue = origDescriptor instanceof SourceValueFieldDescriptor;
			if (! isSourceValue && ! propertyUtils.isReadable(srcObj, name)) {
				String error = String.format("Source Field '%s' is not readable", name);
				throw new FieldCopyException(error);
			}
            fillInDestPropIfNeeded(pair, destClass);

            BUFieldPlan fieldPlan = new BUFieldPlan();
            fieldPlan.srcFd = origDescriptor;
            fieldPlan.destFd = pair.destProp;
            fieldPlan.defaultValue = pair.defaultValue;
            if (destObj != null) {
            	fieldPlan.hasSetterMethod =  propertyUtils.isWriteable(destObj, pair.destFieldName); 
            	fieldPlan.hasSetterMethodIsResolved = true;
            } else {
            	fieldPlan.hasSetterMethod =  false; 
            	fieldPlan.hasSetterMethodIsResolved = false;
            }
            Class<?> srcType = pair.getSrcClass();

            if (beanDetectorSvc.isBeanClass(srcType)) {
            	fieldPlan.isBean = true;
            	Object srcFieldValue = propertyUtils.getSimpleProperty(srcObj, name);
            	if (srcFieldValue == null) {
            		logger.log("lazy on field: %s", name);
            		fieldPlan.lazySubPlanFlag = true; //do later if this field is ever non-null
            	} else {
            		//recursively generate plan
            		fieldPlan.subPlan = doCreateSubPlan(copySpec, pair, srcType, srcFieldValue, state); //**recursion**
            	}
            } else {
            	helperSvc.validateIsAllowed(pair);
            	
    			//handle list, array, list to array, and viceversa
            	fieldPlan.converter = findOrCreateCollectionConverter(fieldPlan, pair, classPlan);
            	
            	//add converter if one matches
        		if (fieldPlan.converter == null) {
        			fieldPlan.converter = converterSvc.findConverter(copySpec, pair, srcObj, copySpec.converterL);
        		}
            }
            tmpL.add(fieldPlan);
		}
        classPlan.fieldPlanL.addAll(tmpL);
		
		//need copySpec and classPlan to have same set of converters
		copySpec.converterL = new ThreadSafeList<>();
		copySpec.converterL.addAll(classPlan.converterL);
		return classPlan;
	}
	
	private ValueConverter findOrCreateCollectionConverter(BUFieldPlan fieldPlan, FieldPair pair, BUClassPlan classPlan) {
		//TODO. fix this. SourceValueDescriptor not supported
		if (pair.srcProp instanceof SourceValueFieldDescriptor) {
			return null;
		}
		
		if (classPlan.converterL == null) {
			classPlan.converterL = new ThreadSafeList<>();
		}
		
    	ValueConverter converter = converterSvc.addListConverterIfNeeded(fieldPlan, pair, classPlan, classPlan.destClass);
    	if (converter != null) {
    		return converter;
    	}
		converter = converterSvc.addArrayListConverterIfNeeded(fieldPlan, pair, classPlan, classPlan.destClass);
    	if (converter != null) {
    		return converter;
    	}
		converter = converterSvc.addArrayConverterIfNeeded(fieldPlan, pair, classPlan, classPlan.destClass);
    	if (converter != null) {
    		return converter;
    	}
		converter = converterSvc.addListArrayConverterIfNeeded(fieldPlan, pair, classPlan, classPlan.destClass);
    	if (converter != null) {
    		return converter;
    	}
    	return null;
	}
	

	private BUClassPlan doCreateSubPlan(CopySpec copySpec, FieldPair pair, Class<?> srcType, Object srcFieldValue, PlanCreateState state) throws Exception {
        Class<?> destType = pair.getDestClass();
		
        //look if client passed a mapping
        List<FieldPair> subFieldPairs;
        FieldCopyMapping mapping = findMapping(pair, copySpec.mappingL);
        if (mapping != null) {
        	subFieldPairs = mapping.getFieldPairs();
        } else {
        	subFieldPairs = this.buildAutoCopyPairs(new TargetPair(srcType, destType));
        }
        state.runawayCounter++;
		return buildClassPlan(srcFieldValue, null, srcType, destType, subFieldPairs, copySpec, state);
	}
	private FieldCopyMapping findMapping(FieldPair pair, List<FieldCopyMapping> mappingL) throws Exception {
		if (CollectionUtils.isEmpty(mappingL)) {
			return null;
		}
		Class<?> srcClass = pair.getSrcClass();
		
		for(FieldCopyMapping mapping: mappingL) {
			if (mapping.getClazzSrc().equals(srcClass)) {
				if (pair.destProp == null) {
					throw new IllegalArgumentException("fix later");
				}
				Class<?> destClass = pair.getDestClass();
				if (mapping.getClazzDest().equals(destClass)) {
					return mapping;
				}
			}
		}
		return null;
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
	

	@Override
	public <T> T copyFields(CopySpec copySpec, Class<T> destClass) {
		T destObj = (T) FieldCopyUtils.createObject(destClass);
		copySpec.destObj = destObj;
		copyFields(copySpec);
		return destObj;
	}

	private boolean executePlan(BUExecutePlan execPlan, int runawayCounter)  {
		if (runawayCounter > execPlan.copySpec.options.maxRecursionDepth) {
			String error = String.format("maxRecursionDepth exceeded. There may be a circular reference.");
			throw new FieldCopyException(error);
		}
		
		boolean b = false;
		try {
			b = doExecutePlan(execPlan, runawayCounter);
		} catch (Exception e) {
			String s = execPlan.inConverter ? " in converter:" : ":";
			String className = String.format("'%s'", execPlan.classPlan.srcClass.getSimpleName());
			String field = execPlan.currentFieldName == null ? "" : " field '" + execPlan.currentFieldName + "'";
			String msg = String.format("Exception in %s %s: %s%s %s", className, field,
					e.getClass().getSimpleName(), s, e.getMessage());
			throw new FieldCopyException(msg, e);
		}
		return b;
	}

	private boolean doExecutePlan(BUExecutePlan execPlan, int runawayCounter) throws Exception {
		boolean ok = true;
		metrics.incrementPlanExecutionCount();
		BUClassPlan classPlan = execPlan.classPlan;
		Iterator<BUFieldPlan> iter = classPlan.fieldPlanL.iterator();
		while(iter.hasNext()) {
			BUFieldPlan fieldPlan = iter.next();
			String name = fieldPlan.srcFd.getName();
			execPlan.currentFieldName = name;
    		Object value;
    		if (fieldPlan.srcFd instanceof SourceValueFieldDescriptor) {
    			SourceValueFieldDescriptor svfd = (SourceValueFieldDescriptor) fieldPlan.srcFd;
    			value = svfd.getValue();
    			
    		} else {
    			value = propertyUtils.getSimpleProperty(execPlan.srcObject, name);
    		}
    		
    		logger.log("  field %s: %s", name, getLoggableString(value));
    		Class<?> srcClass = fieldPlan.getSrcClass();
    		
			if (fieldPlan.converter != null) {
				Class<?> destClass = fieldPlan.getDestClass();
				
				ConverterContext ctx = new ConverterContext();
				ctx.destClass = destClass;
				ctx.srcClass = srcClass;
				ctx.copySvc = this;
				ctx.copyOptions = execPlan.copySpec.options;
				//ctx.beanDetectorSvc = this.beanDetectorSvc;  remove from context!
				ctx.runawayCounter = runawayCounter;
				addConverterAndMappingLists(ctx, execPlan);
				execPlan.inConverter = true;
				value = fieldPlan.converter.convertValue(execPlan.srcObject, value, ctx);
				execPlan.inConverter = false;
			}
			
			if (value == null) {
				value = fieldPlan.defaultValue;
			}
			
			if (fieldPlan.lazySubPlanFlag && value != null) {
				FieldPair pair = new FieldPair();
				pair.defaultValue = fieldPlan.defaultValue;
				pair.destFieldName = fieldPlan.destFd.getName();
				pair.destProp = fieldPlan.destFd;
				pair.srcProp = fieldPlan.srcFd;
				logger.log("lazy-generate-plan %s", pair.destFieldName);
				PlanCreateState state = new PlanCreateState();
				fieldPlan.subPlan = doCreateSubPlan(execPlan.copySpec, pair, srcClass, value, state);
				if (fieldPlan.subPlan != null) {
					fieldPlan.lazySubPlanFlag = false;
					metrics.incrementLazyPlanGenerationCount();
				}
			}
			
			if (fieldPlan.subPlan != null) {
				BUExecutePlan subexec = new BUExecutePlan();
				subexec.srcObject= value;
				subexec.classPlan = fieldPlan.subPlan;
				subexec.copySpec = execPlan.copySpec;
				
				//use the mapping, which has fields, autocopy flag etc
				String destFieldName = fieldPlan.destFd.getName();
				Object destValue = propertyUtils.getSimpleProperty(execPlan.destObj, destFieldName);
				if (destValue == null) {
					Class<?> destClass = fieldPlan.getDestClass();
					destValue = createObject(destClass);
					doCopyProperty(fieldPlan, execPlan.destObj, destFieldName, destValue);
				}
				
				subexec.destObj = destValue;
				boolean b = this.executePlan(subexec, runawayCounter + 1);
				if (!b) {
					return false;
				}
			} else {
				String destFieldName = fieldPlan.destFd.getName();
				doCopyProperty(fieldPlan, execPlan.destObj, destFieldName, value);
				metrics.incrementFieldExecutionCount();
			}
		}
		return ok;
	}
	
	/**
	 * Copy destValue into the destination property. Use setter or field reflection to set the value.
	 */
	private void doCopyProperty(BUFieldPlan fieldPlan, Object destObj, String destFieldName, Object destValue) throws Exception {
		boolean useSetter = false;
		if (! fieldPlan.hasSetterMethodIsResolved) {
			useSetter = propertyUtils.isWriteable(destObj, destFieldName); 
		} else {
			useSetter = fieldPlan.hasSetterMethod;
		}
		
		if (useSetter) {
			beanUtil.copyProperty(destObj, destFieldName, destValue);
		} else {
			fieldSetterSvc.setField(destObj, destFieldName, destValue);
		}
	}

	private Object createObject(Class<?> clazzDest) throws InstantiationException, IllegalAccessException {
		return clazzDest.newInstance();
	}
	private void addConverterAndMappingLists(ConverterContext ctx, BUExecutePlan execPlan) {
		if (CollectionUtils.isNotEmpty(execPlan.copySpec.mappingL)) {
			ctx.mappingL = new ArrayList<>();
			ctx.mappingL.addAll(execPlan.copySpec.mappingL);
		}
		if (ThreadSafeList.isNotEmpty(execPlan.copySpec.converterL)) {
			ctx.converterL = new ArrayList<>();
			execPlan.copySpec.converterL.addIntoOtherList(ctx.converterL);
//			ctx.converterL.addAll(execPlan.copySpec.converterL);
		}
	}

	public boolean isEnablePlanCache() {
		return enablePlanCache;
	}

	public void setEnablePlanCache(boolean enablePlanCache) {
		this.enablePlanCache = enablePlanCache;
	}

	@Override
	public void setMetrics(CopyMetrics metrics) {
		this.metrics = metrics;
	}

	@Override
	public CopyMetrics getMetrics() {
		return metrics;
	}

	@Override
	public FieldDescriptor resolveSourceField(String srcField, TargetPair targetPair) {
		return null; //not supported
	}
}