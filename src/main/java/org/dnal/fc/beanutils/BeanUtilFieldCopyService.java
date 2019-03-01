package org.dnal.fc.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.dnal.fc.CopyOptions;
import org.dnal.fc.core.FieldFilter;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldDescriptor;
import org.dnal.fc.core.FieldPair;
import org.dnal.fc.core.FieldRegistry;
import org.dnal.fieldcopy.FieldCopyException;
import org.dnal.fieldcopy.ValueTests.FieldCopyUtils;
import org.dnal.fieldcopy.log.SimpleLogger;

public class BeanUtilFieldCopyService implements FieldCopyService {
		private SimpleLogger logger;
		private BeanUtilsBean beanUtil;
		private PropertyUtilsBean propertyUtils;
		private FieldRegistry registry;
		private FieldFilter fieldFilter;
		
		public BeanUtilFieldCopyService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
		}

		@Override
		public List<FieldPair> buildAutoCopyPairs(Object sourceObj, Object destObj)  {
            List<FieldPair> fieldPairs = registry.findAutoCopyInfo(sourceObj.getClass(), destObj.getClass());
			if (fieldPairs != null) {
				return fieldPairs;
			}
			
            final PropertyDescriptor[] arSrc = propertyUtils.getPropertyDescriptors(sourceObj);
            final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(destObj);
    		
            fieldPairs = new ArrayList<>();
            for (int i = 0; i < arSrc.length; i++) {
            	PropertyDescriptor pd = arSrc[i];
            	if (! fieldFilter.shouldProcess(sourceObj, pd.getName())) {
            		continue; // No point in trying to set an object's class
                }

            	String targetFieldName = findMatchingField(arDest, pd.getName());
            	
            	FieldPair pair = new FieldPair();
            	pair.srcProp = new BeanUtilsFieldDescriptor(pd);
            	pair.destFieldName = targetFieldName;
            	fieldPairs.add(pair);
            }
			
			registry.registerAutoCopyInfo(sourceObj.getClass(), destObj.getClass(), fieldPairs);
            return fieldPairs;
		}
		
		@Override
		public void copyFields(Object sourceObj, Object destObj, List<FieldPair> fieldPairs, CopyOptions options)  {
			try {
				doCopyFields(sourceObj, destObj, fieldPairs, options);
			} catch (Exception e) {
				throw new FieldCopyException(e.getMessage());
			}
		}
		
		
		private String findMatchingField(PropertyDescriptor[] arDest, String name) {
            for (int i = 0; i < arDest.length; i++) {
            	
            	PropertyDescriptor pd = arDest[i];
            	if (pd.getName().equals(name)) {
            		return pd.getName();
            	}
            }
            return null;
		}

		private void doCopyFields(Object sourceObj, Object destObj, List<FieldPair> fieldPairs, CopyOptions options) throws IllegalAccessException, InvocationTargetException {
			if (sourceObj == null) {
				String error = String.format("copyFields. NULL passed to sourceObj");
				throw new FieldCopyException(error);
			}
			if (destObj == null) {
				String error = String.format("copyFields. NULL passed to destObj.");
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
                		propertyUtils.isWriteable(dest, name)) {
                	try {
                		final Object value = propertyUtils.getSimpleProperty(orig, name);
                		
            			if (options.logEachCopy) {
            				String tmp = FieldCopyUtils.objToString(value);
            				logger.log("%s -> %s = %s", pair.srcProp.getName(), pair.destFieldName, tmp);
            			}
                		
                		beanUtil.copyProperty(dest, pair.destFieldName, value);
                	} catch (final NoSuchMethodException e) {
                		// Should not happen
                	}
                }
			}
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
		
//		public Map<String,Object> convertToMap(Object sourceObj) {
//			registry.prepareObj(sourceObj);
//			Map<String,Object> map = new HashMap<>();
//			ClassFieldInfo info1 = registry.find(sourceObj.getClass());
//			for(String fieldName: info1.fieldMap.keySet()) {
//				Value val1 = info1.getValueField(sourceObj, fieldName);
//				map.put(fieldName, val1.getRawObject());
//			}
//			return map;
//		}
	}