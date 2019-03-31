package org.dnal.fieldcopy.planner;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.CollectionUtils;
import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;

public class PlannerService extends PlannerServiceBase {
	private Map<String,ZClassPlan> executionPlanMap = new ConcurrentHashMap<>();
	private boolean enablePlanCache = true;
	private ZConverterService converterSvc;

	public PlannerService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
		super(logger, registry, fieldFilter);
		this.converterSvc = new ZConverterService(logger, this.beanDetectorSvc, this);
	}
	
	public int getPlanCacheSize() {
		return executionPlanMap.size();
	}
	public ZClassPlan findPlan(String srcClassName) {
		for(String key: executionPlanMap.keySet()) {
			if (key.contains(srcClassName)) {
				return executionPlanMap.get(key);
			}
		}
		return null;
	}
	
	@Override
	public void copyFields(CopySpec copySpec) {
		logger.log("PLAN!");
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
		
		ZClassPlan classPlan = getOrCreatePlan(copySpec);
		
		try {
			logger.log("%s->%s: plan: %d fields", copySpec.sourceObj.getClass(), 
					copySpec.destObj.getClass(), classPlan.fieldPlanL.size());
			ZExecPlan execPlan = new ZExecPlan();
			execPlan.srcObject = sourceObj;
			execPlan.destObj = destObj;
			execPlan.classPlan = classPlan;
			execPlan.copySpec = copySpec;
			//TODO anything else to propagate?
			//propogateStuff(execSpec, copySpec);
			
			this.executePlan(execPlan, copySpec.runawayCounter);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private ZClassPlan getOrCreatePlan(CopySpec copySpec) {
		Object sourceObj = copySpec.sourceObj;
		Object destObj = copySpec.destObj;
		
		ZClassPlan classPlan = null;
		if (enablePlanCache) {
			if (copySpec.executionPlanCacheKey == null) {
				copySpec.executionPlanCacheKey = generateExecutionPlanCacheKey(copySpec);
			}
			classPlan = executionPlanMap.get(copySpec.executionPlanCacheKey);
		}
		
		if (classPlan == null) {
			try {
				classPlan = buildClassPlan(sourceObj, destObj, sourceObj.getClass(), destObj.getClass(), copySpec.fieldPairs, copySpec);
			} catch (Exception e) {
				//throw FCException with inner !!!!!!!!!!!!!
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (enablePlanCache) {
				executionPlanMap.put(copySpec.executionPlanCacheKey, classPlan);
			}
		}
		return classPlan;
	}

	private ZClassPlan buildClassPlan(Object srcObj, Object destObj, Class<?> srcClass, Class<?> destClass, List<FieldPair> fieldPairs, CopySpec copySpec) throws Exception {
		logger.log("BUILDPLAN!");
		if (srcObj == null) {
			String error = String.format("buildClassPlan. srcObj is NULL");
			throw new FieldCopyException(error);
		}
		//destObj can be null
		
		ZClassPlan classPlan = new ZClassPlan();
		classPlan.srcClass = srcClass;
		classPlan.destClass = destClass;
		if (CollectionUtils.isNotEmpty(copySpec.converterL)) {
			classPlan.converterL.addAll(copySpec.converterL);
		}

		for(FieldPair pair: fieldPairs) {
			final FieldDescriptor origDescriptor = pair.srcProp;
			final String name = origDescriptor.getName();
			
			//check for readability and writability
			if (! propertyUtils.isReadable(srcObj, name)) {
				String error = String.format("Source Field '%s' is not readable", name);
				throw new FieldCopyException(error);
			}
			if (destObj != null && !propertyUtils.isWriteable(destObj, pair.destFieldName)) {
				String error = String.format("Destination Field '%s' is not writeable", name);
				throw new FieldCopyException(error);
			}				
            fillInDestPropIfNeeded(pair, destClass);

            ZFieldPlan fieldPlan = new ZFieldPlan();
            fieldPlan.srcFd = origDescriptor;
            fieldPlan.destFd = pair.destProp;
            Class<?> srcType = pair.getSrcClass();

            if (beanDetectorSvc.isBeanClass(srcType)) {
            	fieldPlan.isBean = true;
            	Object srcFieldValue = propertyUtils.getSimpleProperty(srcObj, name);
            	if (srcFieldValue == null) {
            		logger.log("lazy on field: %s", name);
            		fieldPlan.lazySubPlanFlag = true; //do later if this field is ever non-null
            	} else {
            		//recursively generate plan
            		fieldPlan.subPlan = doCreateSubPlan(copySpec, pair, srcType, srcFieldValue); //**recursion**
            	}
            } else {
            	validateIsAllowed(pair);
            	
    			//handle list, array, list to array, and viceversa
            	fieldPlan.converter = findOrCreateCollectionConverter(fieldPlan, pair, classPlan);
            	
            	//add converter if one matches
        		if (fieldPlan.converter == null) {
        			fieldPlan.converter = converterSvc.findConverter(copySpec, pair, srcObj, copySpec.converterL);
        		}
            }
            classPlan.fieldPlanL.add(fieldPlan);
		}
		
		//need copySpec and classPlan to have same set of converters
		copySpec.converterL = new ArrayList<>(classPlan.converterL);
		return classPlan;
	}
	
	private ValueConverter findOrCreateCollectionConverter(ZFieldPlan fieldPlan, FieldPair pair, ZClassPlan classPlan) {
		if (classPlan.converterL == null) {
			classPlan.converterL = new ArrayList<>();
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
	

	private ZClassPlan doCreateSubPlan(CopySpec copySpec, FieldPair pair, Class<?> srcType, Object srcFieldValue) throws Exception {
        Class<?> destType = pair.getDestClass();
		
        //look if client passed a mapping
        List<FieldPair> subFieldPairs;
        FieldCopyMapping mapping = findMapping(pair, copySpec.mappingL);
        if (mapping != null) {
        	subFieldPairs = mapping.getFieldPairs();
        } else {
        	subFieldPairs = this.buildAutoCopyPairs(srcType, destType);
        }
		return buildClassPlan(srcFieldValue, null, srcType, destType, subFieldPairs, copySpec);
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
		T destObj = (T) createDestObject(destClass);
		copySpec.destObj = destObj;
		copyFields(copySpec);
		return destObj;
	}

	@SuppressWarnings("unchecked")
	private <T> T createDestObject(Class<T> destClass) {
		T obj = null;
		try {
			obj = (T) destClass.newInstance();
		} catch (InstantiationException e) {
			throw new FieldCopyException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new FieldCopyException(e.getMessage());
		}
		return obj;
	}
	
	
	private boolean executePlan(ZExecPlan execPlan, int runawayCounter)  {
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

	private boolean doExecutePlan(ZExecPlan execPlan, int runawayCounter) throws Exception {
		boolean ok = true;
		ZClassPlan classPlan = execPlan.classPlan;
		for(ZFieldPlan fieldPlan: classPlan.fieldPlanL) {
			String name = fieldPlan.srcFd.getName();
			execPlan.currentFieldName = name;
    		Object value = propertyUtils.getSimpleProperty(execPlan.srcObject, name);
    		
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
				fieldPlan.subPlan = doCreateSubPlan(execPlan.copySpec, pair, srcClass, value);
				if (fieldPlan.subPlan != null) {
					fieldPlan.lazySubPlanFlag = false;
				}
			}
			
			if (fieldPlan.subPlan != null) {
				ZExecPlan subexec = new ZExecPlan();
				subexec.srcObject= value;
				subexec.classPlan = fieldPlan.subPlan;
				subexec.copySpec = execPlan.copySpec;
				
				//use the mapping, which has fields, autocopy flag etc
				String destFieldName = fieldPlan.destFd.getName();
				Object destValue = propertyUtils.getSimpleProperty(execPlan.destObj, destFieldName);
				if (destValue == null) {
					Class<?> destClass = fieldPlan.getDestClass();
					destValue = createObject(destClass);
					beanUtil.copyProperty(execPlan.destObj, destFieldName, destValue);
				}
				
				subexec.destObj = destValue;
				boolean b = this.executePlan(subexec, runawayCounter + 1);
				if (!b) {
					return false;
				}
			} else {
				String destFieldName = fieldPlan.destFd.getName();
				beanUtil.copyProperty(execPlan.destObj, destFieldName, value);
			}
		}
		return ok;
	}
	private Object createObject(Class<?> clazzDest) throws InstantiationException, IllegalAccessException {
		return clazzDest.newInstance();
	}
	private void addConverterAndMappingLists(ConverterContext ctx, ZExecPlan execPlan) {
		if (CollectionUtils.isNotEmpty(execPlan.copySpec.mappingL)) {
			ctx.mappingL = new ArrayList<>();
			ctx.mappingL.addAll(execPlan.copySpec.mappingL);
		}
		if (CollectionUtils.isNotEmpty(execPlan.copySpec.converterL)) {
			ctx.converterL = new ArrayList<>();
			ctx.converterL.addAll(execPlan.copySpec.converterL);
		}
	}

	public boolean isEnablePlanCache() {
		return enablePlanCache;
	}

	public void setEnablePlanCache(boolean enablePlanCache) {
		this.enablePlanCache = enablePlanCache;
	}
}