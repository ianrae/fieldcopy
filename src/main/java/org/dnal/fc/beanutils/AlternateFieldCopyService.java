package org.dnal.fc.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.dnal.fc.core.CopySpec;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldFilter;
import org.dnal.fc.core.FieldPair;
import org.dnal.fc.core.FieldRegistry;
import org.dnal.fieldcopy.FieldCopyException;
import org.dnal.fieldcopy.log.SimpleLogger;

/**
 * An implementation of FieldCopyService that uses Apache BeanUtils to do the
 * copying.
 * 
 * This version operates in two steps. First it generates a plan, and then
 * it executes the plan.
 * 
 * @author Ian Rae
 *
 */
public class AlternateFieldCopyService implements FieldCopyService {
		private SimpleLogger logger;
		private BeanUtilsBean beanUtil;
		private PropertyUtilsBean propertyUtils;
		private FieldRegistry registry;
		private FieldFilter fieldFilter;
		private FastBeanUtilFieldCopyService fastSvc;
		private Map<String,ExecuteCopyPlan> executionPlanMap = new HashMap<>();
		
		public AlternateFieldCopyService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
			this.fastSvc = new FastBeanUtilFieldCopyService(logger, fieldFilter);
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
			
			ExecuteCopyPlan execSpec = executionPlanMap.get(copySpec.executionPlanCacheKey);
			if (execSpec == null) {
				execSpec = fastSvc.generateExecutePlan(copySpec);
				executionPlanMap.put(copySpec.executionPlanCacheKey, execSpec);
			}
			boolean b = fastSvc.executePlan(copySpec, execSpec, this, runawayCounter);
			logger.log("fast: %b", b);
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
	}