package org.dnal.fieldcopy.planner;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopyFactory;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsBeanDetectorService;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;
import org.junit.Test;

/**
 * Rewrite based on the idea of creating a recursive execution plan.
 * For each field to be copied we create a FieldPlan that defines converters and other
 * conversion parameters.
 * A field that is a bean contains a sub-plan.
 * 
 * However, BeanUtil API is based on objects, not classes. As we inspect the fields to 
 * build the plan, if we encounter a null value for a bean field, we can't generate a 
 * sub-plan for it.  When this occurrs we set the lazyGenerationNeeded flag to true, so 
 * that when the plan is executed we can generate the sub-plan then.
 *  * this may never occur if the field is always null
 *  * when we lazily create the plan, must do it in thread-safe way
 * 
 * plan backoff -- this is the concept that once we generate a full tree of plan and sub-plans,
 * we may notice that the leaf sub-plans don't have any converters or other FieldCopy features.
 * They could be copied using BeanUtils directly.  So we can set a directMode = true and 
 * set the sub-plan to null.  This can eventually be propagated upward to parent sub-plans.
 * It may end up that no sub-plans are needed at all -- a much faster performance.
 * 
 * @author Ian Rae
 *
 */
public class PlannerTests extends BaseTest {
	
	public static class ZClassPlan {
		public Class<?> srcClass;
		public Class<?> destClass;
		public List<ZFieldPlan> fieldPlanL = new ArrayList<>();
	}
	public static class ZFieldPlan {
		public FieldDescriptor srcFd;
		public FieldDescriptor destFd;
		public ValueConverter conv;
		public ZClassPlan subPlan; //null if not-bean
		//public boolean directMode; //later when we support plan backoff
		public boolean lazySubPlanFlag = false; 
	}
	
	public static class PlannerSvc implements FieldCopyService {
		
		private SimpleLogger logger;
		private FieldRegistry registry;
		private BeanUtilsBean beanUtil;
		private PropertyUtilsBean propertyUtils;
		private FieldFilter fieldFilter;
		private BeanUtilsBeanDetectorService beanDetectorSvc;

		public PlannerSvc(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
			this.beanDetectorSvc = new BeanUtilsBeanDetectorService();
			
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
			
            fieldPairs = buildAutoCopyPairsNoRegister(class1, class2);
			registry.registerAutoCopyInfo(class1, class2, fieldPairs);
            return fieldPairs;
		}
		
		public List<FieldPair> buildAutoCopyPairsNoRegister(Class<? extends Object> class1, Class<? extends Object> class2) {
            final PropertyDescriptor[] arSrc = propertyUtils.getPropertyDescriptors(class1);
            final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(class2);
    		
            List<FieldPair> fieldPairs = new ArrayList<>();
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
//			if (runawayCounter > options.maxRecursionDepth) {
//				String error = String.format("maxRecursionDepth exceeded. There may be a circular reference.");
//				throw new FieldCopyException(error);
//			}
			
			
			for(FieldPair pair: copySpec.fieldPairs) {
	            final FieldDescriptor origDescriptor = pair.srcProp;
	            final String name = origDescriptor.getName();
	            
	            if (propertyUtils.isReadable(sourceObj, name) &&
	            		propertyUtils.isWriteable(destObj, pair.destFieldName)) {
	            	try {
	            		fillInDestPropIfNeeded(pair, destObj.getClass());
	            		
	            		BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
	            		Class<?> srcType = fd1.pd.getPropertyType();
	            		
	            		if (beanDetectorSvc.isBeanClass(srcType)) {
	            			
	            		} else {
	            			
	            		}
	            		
//	            		
//	            		converterSvc.addListConverterIfNeeded(pair, copySpec, destObj.getClass());
//	            		converterSvc.addArrayListConverterIfNeeded(pair, copySpec, destObj.getClass());
//	            		converterSvc.addArrayConverterIfNeeded(pair, copySpec, destObj.getClass());
//	            		converterSvc.addListArrayConverterIfNeeded(pair, copySpec, destObj.getClass());
	            		
//	            		//a mapping is an explicit set of instructions for copying sub-objects (i.e. sub-beans)
//	            		FieldCopyMapping mapping = findOrGenerateMapping(pair, mappingL, outerSvc, copySpec);
//	            		if (mapping != null) {
//	            			FieldPlan fspec = new FieldPlan();
//	            			fspec.pair = pair;
//	            			fspec.mapping = mapping;
//	            			execspec.fieldL.add(fspec);
//	            		} else {
//	            			validateIsAllowed(pair);
//	            			
//	            			FieldPlan fspec = new FieldPlan();
//	            			fspec.pair = pair;
//	            			fspec.converter = converterSvc.findConverter(copySpec, pair, orig, copySpec.converterL);
//	            			execspec.fieldL.add(fspec);
//	            		}
	            		
	            	} catch (final Exception e) { //was NoSuchMethodException
	            		// Should not happen
	            	}
	            }
			}
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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void dumpFields(Object sourceObj) {
			// TODO Auto-generated method stub
			
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
		public void addBuiltInConverter(ValueConverter converter) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String generateExecutionPlanCacheKey(CopySpec spec) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	
	
	public static class A {
		private String name1;
		private String name2;
		
		public A(String name1, String name2) {
			super();
			this.name1 = name1;
			this.name2 = name2;
		}
		public String getName1() {
			return name1;
		}
		public void setName1(String name1) {
			this.name1 = name1;
		}
		public String getName2() {
			return name2;
		}
		public void setName2(String name2) {
			this.name2 = name2;
		}
	}
	public static class ADTO {
		private String name1;
		private String name2;
		
		public String getName1() {
			return name1;
		}
		public void setName1(String name1) {
			this.name1 = name1;
		}
		public String getName2() {
			return name2;
		}
		public void setName2(String name2) {
			this.name2 = name2;
		}
	}
	
	//add class B and then C
	//C should have a date field. then test that builtIn converter for dates gets applied to C
	
	public static class PlannerFactory extends DefaultCopyFactory	 {
		private static CopyFactory theSingleton;


		@Override
		public FieldCopyService createCopyService() {
			SimpleLogger logger = createLogger();
			FieldRegistry registry = new FieldRegistry();
			FieldFilter fieldFilter = createFieldFilter();
			PlannerSvc copySvc = new PlannerSvc(logger, registry, fieldFilter);
			return copySvc;
		}
		
		public static CopyFactory Factory() {
			if (theSingleton == null) {
				theSingleton = new PlannerFactory();
			}
			return theSingleton;
		}
	}
	
	
	@Test
	public void test() {
		A src = new A("bob", "smith");
		ADTO dest = new ADTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
	}

	@Override
	protected FieldCopier createCopier() {
		PlannerFactory.setLogger(new SimpleConsoleLogger());
		PlannerFactory.Factory().createLogger().enableLogging(true);
		
		return PlannerFactory.Factory().createCopier();
	}
}
