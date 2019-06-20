package org.dnal.fieldcopy.propertyloader;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldCopyUtils;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.core.TargetPair;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.metrics.CopyMetrics;
import org.dnal.fieldcopy.metrics.DoNothingMetrics;
import org.dnal.fieldcopy.service.beanutils.BUBeanDetectorService;
import org.dnal.fieldcopy.service.beanutils.BUConverterService;
import org.dnal.fieldcopy.service.beanutils.BUFieldSetterService;
import org.dnal.fieldcopy.service.beanutils.BUHelperService;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;
import org.dnal.fieldcopy.util.ThreadSafeList;

public class PropertyLoaderService implements FieldCopyService {

		protected SimpleLogger logger;
		protected FieldRegistry registry;
		protected BeanUtilsBean beanUtil;
		protected PropertyUtilsBean propertyUtils;
		protected FieldFilter fieldFilter;
		protected BUBeanDetectorService beanDetectorSvc;
		protected BUHelperService helperSvc;
		private BUConverterService converterSvc;
		private CopyMetrics metrics = new DoNothingMetrics();
		private BUFieldSetterService fieldSetterSvc;

		public PropertyLoaderService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
			this.beanDetectorSvc = new BUBeanDetectorService();
			this.helperSvc = new BUHelperService(logger);
			this.converterSvc = new BUConverterService(logger, this.beanDetectorSvc, this);
			this.fieldSetterSvc = new BUFieldSetterService(logger);
		}

		@Override
		public List<FieldPair> buildAutoCopyPairs(TargetPair targetPair, CopyOptions options) {
			Object sourceObj = targetPair.getSrcObj();
			if (! (sourceObj instanceof PropertyLoader)) {
				String err = String.format("sourceObj is not be a PropertyLoader");
				throw new FieldCopyException(err);
			}
			
			Class<?> class1 = sourceObj.getClass();
			Class<?> class2 = targetPair.getDestClass();
			List<FieldPair> fieldPairs = registry.findAutoCopyInfo(class1, class2);
			if (fieldPairs != null) {
				return fieldPairs;
			}

			fieldPairs = buildAutoCopyPairsNoRegister(class2);
			registry.registerAutoCopyInfo(class1, class2, fieldPairs);
			return fieldPairs;
		}

		private List<FieldPair> buildAutoCopyPairsNoRegister(Class<? extends Object> destClass) {
			final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(destClass);

			List<FieldPair> fieldPairs = new ArrayList<>();
			for (int i = 0; i < arDest.length; i++) {
				PropertyDescriptor pd = arDest[i];
				if (! fieldFilter.shouldProcess(destClass, pd.getName())) {
					continue; // No point in trying to set an object's class
				}

				//since we have no way to enumerate all properties we'll
				//generate field pairs for destClass's fields
				FieldPair pair = new FieldPair();
				pair.srcProp = new ConfigFieldDescriptor(pd.getName());
				pair.destFieldName = pd.getName();
				pair.destProp = new BeanUtilsFieldDescriptor(pd);
				fieldPairs.add(pair);
			}
			return fieldPairs;
		}
		

		@Override
		public FieldDescriptor resolveSourceField(String srcField, TargetPair targetPair, CopyOptions options) {
			return new ConfigFieldDescriptor(srcField);
		}

		@Override
		public void copyFields(CopySpec copySpec) {
			Object destObj = copySpec.destObj;

			for(FieldPair pair: copySpec.fieldPairs) {
				final FieldDescriptor origDescriptor = pair.srcProp;
				final String name = origDescriptor.getName();
				//				state.currentFieldName = name; //for logging errors

				boolean hasSetterMethod = propertyUtils.isWriteable(destObj, pair.destFieldName);
				fillInDestPropIfNeeded(pair, destObj.getClass());
				
				//	            validateIsAllowed(pair);

				ValueConverter converter = findAConverter(copySpec, pair);

				//val = findProperty ....
				PropertyLoader loader = (PropertyLoader) copySpec.sourceObj;
				String pname = pair.srcProp.getName();
				Object value = loader.load(pname);
				if (value == null && pair.defaultValue != null) {
					value = pair.defaultValue;
				}
				
				if (converter != null) {
					Class<?> destClass = pair.getDestClass();
					
					ConverterContext ctx = new ConverterContext();
					ctx.destClass = destClass;
					ctx.srcClass = String.class;
					ctx.copySvc = this;
					ctx.copyOptions = copySpec.options;
					ctx.runawayCounter = 1; //no recursion here
					addConverterAndMappingLists(ctx, copySpec);
//					execPlan.inConverter = true;
					Object obj = converter.convertValue(copySpec.sourceObj, value, ctx);
					value = (obj == null) ? null : obj.toString();
//					execPlan.inConverter = false;
				}
				
				//store in destObj
				String destFieldName = pair.destFieldName;
				if (hasSetterMethod) {
					try {
						beanUtil.copyProperty(copySpec.destObj, destFieldName, value);
					} catch (Exception ex) {
						String err = String.format("copyProperty field '%s' failed. %s", destFieldName, ex.getMessage());
						throw new FieldCopyException(err, ex);
					}
				} else {
					fieldSetterSvc.setField(copySpec.destObj, destFieldName, value);
				}
			}
		}
		
		private void addConverterAndMappingLists(ConverterContext ctx, CopySpec copySpec) {
			//mappings are not supported
			if (ThreadSafeList.isNotEmpty(copySpec.converterL)) {
				ctx.converterL = new ArrayList<>();
				copySpec.converterL.addIntoOtherList(ctx.converterL);
			}
		}
		
		private ValueConverter findAConverter(CopySpec copySpec, FieldPair pair)  {
			//lists,arrays not supported for now.
			//handle list, array, list to array, and viceversa
			//	            ValueConverter converter = findOrCreateCollectionConverter(fieldPlan, pair, classPlan);
			
			//add converter if one matches
			FieldInfo sourceField = new FieldInfo();
			sourceField.fieldName = pair.srcProp.getName();
			sourceField.fieldClass = String.class;
			sourceField.beanClass = copySpec.sourceObj.getClass();
			
			FieldInfo destField = new FieldInfo();
			destField.fieldName = pair.destProp.getName();
			destField.fieldClass = pair.getDestClass();
			destField.beanClass = copySpec.destObj.getClass();
			
			return converterSvc.findConverter(sourceField, destField, copySpec.converterL);
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
		

		@Override
		public void addBuiltInConverter(ValueConverter converter) {
			this.converterSvc.addBuiltInConverter(converter);
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
		public void dumpFields(Object sourceObj) {
			helperSvc.dumpFields(sourceObj);
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
		public String generateExecutionPlanCacheKey(CopySpec spec) {
			return helperSvc.generateExecutionPlanCacheKey(spec);
		}
	}