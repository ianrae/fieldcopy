package org.dnal.fieldcopy.planner;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.converter.ConverterContext;
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
 * sub-plan for it.  When this occurs we set the lazyGenerationNeeded flag to true, so 
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
//		public Object srcObject;
//		public Object destObj;
		public Class<?> srcClass;
		public Class<?> destClass;
		public List<ZFieldPlan> fieldPlanL = new ArrayList<>();
	}
	public static class ZFieldPlan {
		public FieldDescriptor srcFd;
		public FieldDescriptor destFd;
		public ValueConverter converter;
		public Object defaultValue = null;
		public ZClassPlan subPlan; //null if not-bean
		//public boolean directMode; //later when we support plan backoff
		public boolean lazySubPlanFlag = false; 

		public Class<?> getSrcClass() {
			BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) srcFd;
			Class<?> srcClass = fd1.pd.getPropertyType();
			return srcClass;
		}
		public Class<?> getDestClass() {
			BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) destFd;
			Class<?> destClass = fd2.pd.getPropertyType();
			return destClass;
		}
	}
	public static class ZExecPlan {
		public Object srcObject;
		public Object destObj;
		public ZClassPlan classPlan;
		public boolean inConverter; //used to make better error messages
		public String currentFieldName;
	}
	
	public static class PlannerService extends PlannerServiceBase {
		private Map<String,ZClassPlan> executionPlanMap = new ConcurrentHashMap<>();
		private boolean enablePlanCache = true;

		public PlannerService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			super(logger, registry, fieldFilter);
		}
		
		public int getPlanCacheSize() {
			return executionPlanMap.size();
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
			
			if (copySpec.executionPlanCacheKey == null) {
				copySpec.executionPlanCacheKey = generateExecutionPlanCacheKey(copySpec);
			}
			ZClassPlan classPlan = executionPlanMap.get(copySpec.executionPlanCacheKey);
			if (! enablePlanCache) {
				classPlan = null;
			}
			if (classPlan == null) {
				try {
					classPlan = this.buildClassPlan(sourceObj, destObj, sourceObj.getClass(), destObj.getClass(), copySpec.fieldPairs);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (! enablePlanCache) {
					executionPlanMap.put(copySpec.executionPlanCacheKey, classPlan);
				}
			} else {
				// do we have anything to propogate?
				//propogateStuff(execSpec, copySpec);
			}
			
			try {
				logger.log("%s->%s: plan: %d fields", copySpec.sourceObj.getClass(), 
						copySpec.destObj.getClass(), classPlan.fieldPlanL.size());
				ZExecPlan execPlan = new ZExecPlan();
				execPlan.srcObject = sourceObj;
				execPlan.destObj = destObj;
				execPlan.classPlan = classPlan;
				
				this.executePlan(execPlan, 1);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		private ZClassPlan buildClassPlan(Object srcObj, Object destObj, Class<?> srcClass, Class<?> destClass, List<FieldPair> fieldPairs) throws Exception {
			logger.log("BUILDPLAN!");
			if (srcObj == null) {
				String error = String.format("buildClassPlan. srcObj is NULL");
				throw new FieldCopyException(error);
			}
			//destObj can be null
			
			ZClassPlan classPlan = new ZClassPlan();
			classPlan.srcClass = srcClass;
			classPlan.destClass = destClass;

			for(FieldPair pair: fieldPairs) {
				final FieldDescriptor origDescriptor = pair.srcProp;
				final String name = origDescriptor.getName();
				
				//check for readability and writability
				if (! propertyUtils.isReadable(srcObj, name)) {
					String error = String.format("Source Field '%s' is not readable", name);
					throw new FieldCopyException(error);
				}
				if (destObj != null) {
					if (!propertyUtils.isWriteable(destObj, pair.destFieldName)) {
						String error = String.format("Destination Field '%s' is not writeable", name);
						throw new FieldCopyException(error);
					}
				}				
	            
	            //TODO add check for is-readable and writer later
	            fillInDestPropIfNeeded(pair, destClass);

	            ZFieldPlan fieldPlan = new ZFieldPlan();
	            fieldPlan.srcFd = origDescriptor;
	            fieldPlan.destFd = pair.destProp;
	            
	            Class<?> srcType = pair.getSrcClass();

	            if (beanDetectorSvc.isBeanClass(srcType)) {
	            	Object srcFieldValue = propertyUtils.getSimpleProperty(srcObj, name);
	            	if (srcFieldValue == null) {
	            		logger.log("lazy on field: %s", name);
	            		fieldPlan.lazySubPlanFlag = true; //do later if this field is ever non-null
	            	} else {
	            		//recursively generate plan
	            		fieldPlan.subPlan = createSubPlan1(pair, srcType, srcFieldValue);
	            	}
	            } else {
        			//handle list
        			//handle array
        			//handle list to array, and viceversa
        			validateIsAllowed(pair);
	            	//add converter if one matches
	            }
	            classPlan.fieldPlanL.add(fieldPlan);
			}
			
			return classPlan;
		}
		
		
		private ZClassPlan createSubPlan1(FieldPair pair, Class<?> srcType, Object srcFieldValue) throws Exception {
            Class<?> destType = pair.getDestClass();
    		
            //!!look if client passed in mapping
            List<FieldPair> subFieldPairs = this.buildAutoCopyPairs(srcType, destType);
    		return buildClassPlan(srcFieldValue, null, srcType, destType, subFieldPairs);
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
					//ctx.copySvc = outerSvc;
					//ctx.copyOptions = spec.options;
					ctx.beanDetectorSvc = this.beanDetectorSvc;
					//addConverterAndMappingLists(ctx, spec);
					execPlan.inConverter = true;
					//value = fieldPlan.converter.convertValue(spec.sourceObj, value, ctx);
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
					logger.log("lazy-gen %s", pair.destFieldName);
					ZClassPlan ff = createSubPlan1(pair, srcClass, value);
					
					if (ff != null) {
						fieldPlan.subPlan = ff;
						fieldPlan.lazySubPlanFlag = false;
					}
				}
				
				if (fieldPlan.subPlan != null) {
					ZExecPlan subexec = new ZExecPlan();
					subexec.srcObject= value;
					subexec.classPlan = fieldPlan.subPlan;
					
					//use the mapping, which has fields, autocopy flag etc
					String destFieldName = fieldPlan.destFd.getName();
					Object destValue = propertyUtils.getSimpleProperty(execPlan.destObj, destFieldName);
					if (destValue == null) {
						BeanUtilsFieldDescriptor fd2 = (BeanUtilsFieldDescriptor) fieldPlan.destFd;
						Class<?> destClass = fd2.pd.getPropertyType();
						
						destValue = createObject(destClass);
						beanUtil.copyProperty(execPlan.destObj, destFieldName, destValue);
					}
					
					subexec.destObj = destValue;
					boolean b = this.executePlan(subexec, runawayCounter + 1);
					if (!b) {
						return false;
					}
					//applyMapping(outerSvc, spec, fieldPlan.pair, spec.sourceObj, spec.destObj, value, fieldPlan.mapping, runawayCounter);
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

//		private void addConverterAndMappingLists(ConverterContext ctx, CopySpec spec) {
//			if (CollectionUtils.isNotEmpty(spec.mappingL)) {
//				ctx.mappingL = new ArrayList<>();
//				ctx.mappingL.addAll(spec.mappingL);
//			}
//			if (CollectionUtils.isNotEmpty(spec.converterL)) {
//				ctx.converterL = new ArrayList<>();
//				ctx.converterL.addAll(spec.converterL);
//			}
//		}

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

		public boolean isEnablePlanCache() {
			return enablePlanCache;
		}

		public void setEnablePlanCache(boolean enablePlanCache) {
			this.enablePlanCache = enablePlanCache;
		}
		
		
	}
	
	
	public static class A {
		private String name1;
		private String name2;
		private B bVal;
		
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
		public B getbVal() {
			return bVal;
		}
		public void setbVal(B bVal) {
			this.bVal = bVal;
		}
	}
	public static class ADTO {
		private String name1;
		private String name2;
		private BDTO bVal;

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
		public BDTO getbVal() {
			return bVal;
		}
		public void setbVal(BDTO bVal) {
			this.bVal = bVal;
		}
	}
	
	public static class B {
		private String title;
		
		public B(String title) {
			super();
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}
	}
	public static class BDTO {
		private String title;
		
		public BDTO() {
		}
		public BDTO(String title) {
			super();
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
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
			PlannerService copySvc = new PlannerService(logger, registry, fieldFilter);
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
	public void testString() {
		A src = new A("bob", "smith");
		ADTO dest = new ADTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
	}
	
	@Test
	public void testSubPlan() {
		A src = new A("bob", "smith");
		B bval = new B("toronto");
		src.setbVal(bval);
		ADTO dest = new ADTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals("toronto", dest.getbVal().getTitle());
		
		log("again..");
		src = new A("bob", "smith");
		bval = new B("toronto");
		src.setbVal(bval);
		dest = new ADTO();
		
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals("toronto", dest.getbVal().getTitle());
	}
	
	@Test
	public void testSubPlanLazy() {
		A src = new A("bob", "smith");
		ADTO dest = new ADTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals(null, dest.getbVal());
		
		log("again..");
		src = new A("bob", "smith");
		B bval = new B("toronto");
		src.setbVal(bval);
		dest = new ADTO();
		
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals("toronto", dest.getbVal().getTitle());
		
		log("again2..");
		src = new A("bob", "smith");
		bval = new B("toronto");
		src.setbVal(bval);
		dest = new ADTO();
		
		copier.copy(src, dest).autoCopy().execute();
	
		assertEquals("bob", dest.getName1());
		assertEquals("smith", dest.getName2());
		assertEquals("toronto", dest.getbVal().getTitle());
		
		PlannerService plannerSvc = (PlannerService) copier.getCopyService();
		assertEquals(1, plannerSvc.getPlanCacheSize());
	}

	@Override
	protected FieldCopier createCopier() {
		PlannerFactory.setLogger(new SimpleConsoleLogger());
		PlannerFactory.Factory().createLogger().enableLogging(true);
		
		return PlannerFactory.Factory().createCopier();
	}
}
