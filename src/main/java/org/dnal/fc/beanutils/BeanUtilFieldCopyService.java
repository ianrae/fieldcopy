package org.dnal.fc.beanutils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.collections.CollectionUtils;
import org.dnal.fc.CopyOptions;
import org.dnal.fc.FieldCopyMapping;
import org.dnal.fc.core.FieldFilter;
import org.dnal.fc.core.CopySpec;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldDescriptor;
import org.dnal.fc.core.FieldPair;
import org.dnal.fc.core.FieldRegistry;
import org.dnal.fc.core.ValueTransformer;
import org.dnal.fieldcopy.FieldCopyException;
import org.dnal.fieldcopy.ValueTests.FieldCopyUtils;
import org.dnal.fieldcopy.log.SimpleLogger;

/**
 * An implementation of FieldCopyService that uses Apache BeanUtils to do the
 * copying.
 * 
 * @author Ian Rae
 *
 */
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
		public void copyFields(CopySpec copySpec)  {
			try {
				doCopyFields(copySpec, 1);
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

		private void doCopyFields(CopySpec copySpec, int runawayCounter) throws Exception {
			Object sourceObj = copySpec.sourceObj;
			Object destObj = copySpec.destObj;
			List<FieldPair> fieldPairs = copySpec.fieldPairs;
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
                		
                		Object value = propertyUtils.getSimpleProperty(orig, name);
                		if (applyMapping(pair, sourceObj, destObj, value, mappingL, options, runawayCounter)) {
                			
                		} else {
                			if (options.logEachCopy) {
                				String tmp = FieldCopyUtils.objToString(value);
                				logger.log("%s -> %s = %s", pair.srcProp.getName(), pair.destFieldName, tmp);
                			}
                			
                			validateIsAllowed(pair, value, dest);
                			value = transformIfPresent(pair, orig, value, copySpec.transformerL);
                			beanUtil.copyProperty(dest, pair.destFieldName, value);
                		}
                		
                	} catch (final NoSuchMethodException e) {
                		// Should not happen
                	}
                }
			}
		}
		
		private Object transformIfPresent(FieldPair pair, Object orig, Object value, List<ValueTransformer> transformerL) {
			if (value == null) {
				return null;
			}
			
			if (CollectionUtils.isNotEmpty(transformerL)) {
				BeanUtilsFieldDescriptor desc = (BeanUtilsFieldDescriptor) pair.destProp;
				Class<?> destClass = desc.pd.getPropertyType();
				//TODO: can we make this faster with a map??
				for(ValueTransformer transformer: transformerL) {
					if (transformer.canHandle(pair.srcProp.getName(), value, destClass)) {
						transformer.setCopySvc(this);
						return transformer.transformValue(pair.srcProp.getName(), orig, value, destClass);
					}
				}
			}
			return value;
		}

		private void validateIsAllowed(FieldPair pair, Object value, Object dest) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
			if (value != null) {
				Class<?> destClass =  isNotAllowed(pair, value, dest);
				if (destClass != null) {
					String err = String.format("Not allowed to copy %s to %s", value.getClass().getName(), destClass.getName());
					throw new FieldCopyException(err);
				}
			}
		}

		/**
		 * Additional rules that we want to enfore.
		 * For example, not allowed to copy an int to a boolean.
		 * 
		 * @return null if allowed, else class that we are NOT allowed to copy to
		 */
		private Class<?> isNotAllowed(FieldPair pair, Object value, Object dest) {
			BeanUtilsFieldDescriptor desc = (BeanUtilsFieldDescriptor) pair.destProp;
			Class<?> type = desc.pd.getPropertyType();
			
			//TODO: this won't work if someone subclasses Integer. use isAssignableFrom!!
			if (value instanceof Number) {
				if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {			
					return type;
				} else if (Date.class.equals(type)) {
					if (Long.class.equals(value.getClass()) || Long.TYPE.equals(value.getClass())) {
					} else {
						return type;
					}
				}
			} else if (value instanceof Date) {
				if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {			
					return type;
				} else if (Integer.class.equals(type) || Integer.TYPE.equals(type)) {			
					return type;
				} else if (Double.class.equals(type) || Double.TYPE.equals(type)) {			
					return type;
				} else if (type.isEnum()) {			
					return type;
				}
			} else if (value.getClass().isEnum()) {
				if (type.isEnum()) {
				} else if (String.class.equals(type)) {
				} else {
					return type;
				}
			} else if (value instanceof Collection) {
				if (Collection.class.isAssignableFrom(type)) {
				} else {
					return type;
				}
			}
			return null;
		}

		private boolean applyMapping(FieldPair pair, Object sourceObj, Object destObj, Object srcValue, List<FieldCopyMapping> mappingL, CopyOptions options, int runawayCounter) throws Exception {
			if (CollectionUtils.isEmpty(mappingL)) {
				return false;
			}
			if (srcValue == null) {
				return true;
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
                		CopySpec spec = new CopySpec();
                		spec.sourceObj = srcValue;
                		spec.destObj = destValue;
                		spec.fieldPairs = mapping.getFieldPairs();
                		spec.mappingL = mappingL;
                		spec.options = options;
                		doCopyFields(spec, runawayCounter + 1);

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
	}