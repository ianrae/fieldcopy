package org.dnal.fieldcopy.service.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleLogger;

/**
 * An implementation of FieldCopyService that uses Apache BeanUtils to do the
 * copying.
 * 
 * This version operates in two steps. First it generates a plan, and then
 * it executes the plan.  The plan is cached so that on subsequent copies it doesn't need to 
 * be re-generated.
 * 
 * @author Ian Rae
 *
 */
public class BeanUtilsFieldCopyService implements FieldCopyService {
		private SimpleLogger logger;
		private BeanUtilsBean beanUtil;
		private PropertyUtilsBean propertyUtils;
		private FieldRegistry registry;
		private FieldFilter fieldFilter;
		private FastBeanUtilFieldCopyService fastSvc;
		private Map<String,ExecuteCopyPlan> executionPlanMap = new HashMap<>();
		
		public BeanUtilsFieldCopyService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
			this.fastSvc = new FastBeanUtilFieldCopyService(logger, fieldFilter);
			
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
			
            final PropertyDescriptor[] arSrc = propertyUtils.getPropertyDescriptors(class1);
            final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(class2);
    		
            fieldPairs = new ArrayList<>();
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
			
			registry.registerAutoCopyInfo(class1, class2, fieldPairs);
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

		@Override
		public void copyFields(CopySpec copySpec)  {
			try {
				doCopyFields(copySpec, 1);
			} catch (Exception e) {
				throw new FieldCopyException(e.getMessage());
			}
		}

		void doCopyFields(CopySpec copySpec, int runawayCounter) throws Exception {
			if (runawayCounter > copySpec.options.maxRecursionDepth) {
				String error = String.format("maxRecursionDepth exceeded. There may be a circular reference.");
				throw new FieldCopyException(error);
			}
			
			if (copySpec.executionPlanCacheKey == null) {
				copySpec.executionPlanCacheKey = generateExecutionPlanCacheKey(copySpec);
			}
			ExecuteCopyPlan execSpec = executionPlanMap.get(copySpec.executionPlanCacheKey);
			if (execSpec == null) {
				execSpec = fastSvc.generateExecutePlan(copySpec);
				executionPlanMap.put(copySpec.executionPlanCacheKey, execSpec);
			}
			logger.log("%s->%s: plan: %d fields", copySpec.sourceObj.getClass(), 
					copySpec.destObj.getClass(), execSpec.fieldL.size());
			fastSvc.executePlan(copySpec, execSpec, this, runawayCounter);
		}

		
		@Override
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

		@Override
		public SimpleLogger getLogger() {
			return logger;
		}

		@Override
		public FieldRegistry getRegistry() {
			return registry;
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

		@Override
		public String generateExecutionPlanCacheKey(CopySpec spec) {
			//NOTE. the following key will not work if you have multiple conversions of the
			//same pair of source,destObj but with different fields, mappings, and converters.
			//If that is the case, you MUST key cacheKey and provide a unique value.

			//if source or destObj are null we will catch it during copy
			String class1Name = spec.sourceObj == null ? "" : spec.sourceObj.getClass().getName();
			String class2Name = spec.destObj == null ? "" : spec.destObj.getClass().getName();
			return String.format("%s--%s", class1Name, class2Name);
		}
	}