package org.dnal.fc.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.dnal.fc.CopyOptions;
import org.dnal.fc.FieldCopyMapping;
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

		@Override
		public void copyFields(Object sourceObj, Object destObj, List<FieldPair> fieldPairs,  List<FieldCopyMapping> mappingL, CopyOptions options)  {
			try {
				doCopyFields(sourceObj, destObj, fieldPairs, mappingL, options);
			} catch (Exception e) {
				throw new FieldCopyException(e.getMessage());
			}
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

		private void doCopyFields(Object sourceObj, Object destObj, List<FieldPair> fieldPairs, List<FieldCopyMapping> mappingL, CopyOptions options) throws Exception {
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
                		if (applyMapping(pair, sourceObj, destObj, value, mappingL, options)) {
                			
                		} else {
                			if (options.logEachCopy) {
                				String tmp = FieldCopyUtils.objToString(value);
                				logger.log("%s -> %s = %s", pair.srcProp.getName(), pair.destFieldName, tmp);
                			}
                			
                			beanUtil.copyProperty(dest, pair.destFieldName, value);
                		}
                		
                	} catch (final NoSuchMethodException e) {
                		// Should not happen
                	}
                }
			}
		}
		
		private boolean applyMapping(FieldPair pair, Object sourceObj, Object destObj, Object srcValue, List<FieldCopyMapping> mappingL, CopyOptions options) throws Exception {
			if (CollectionUtils.isEmpty(mappingL)) {
				return false;
			}

			BeanUtilsFieldDescriptor fd = (BeanUtilsFieldDescriptor) pair.srcProp;
			for(FieldCopyMapping mapping: mappingL) {
				if (mapping.getClazzSrc().equals(fd.pd.getPropertyType())) {
					if (pair.destProp == null) {
						throw new IllegalArgumentException("fix later");
					}
					BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) pair.destProp;
					if (mapping.getClazzDest().equals(fd2.pd.getPropertyType())) {
						//use the mapping, which has fields, autocopy flag etc
                		Object destValue = propertyUtils.getSimpleProperty(destObj, pair.destFieldName);
                		if (destValue == null) {
                			destValue = createObject(mapping.getClazzDest());
                			beanUtil.copyProperty(destObj, pair.destFieldName, destValue);
                		}
                		
                		//**recursion**
                		copyFields(srcValue, destValue, mapping.getFieldPairs(),  mappingL, options);

						return true;
					}
				}
			}
			return false;
		}
		
		private Object createObject(Class<?> clazzDest) throws InstantiationException, IllegalAccessException {
			return clazzDest.newInstance();
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