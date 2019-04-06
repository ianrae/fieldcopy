package org.dnal.fieldcopy.proploader;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.beanutils.converters.DateConverter;
import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.CopierFactory;
import org.dnal.fieldcopy.DefaultValueTests.Dest;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.core.TargetPair;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.metrics.CopyMetrics;
import org.dnal.fieldcopy.service.beanutils.BUBeanDetectorService;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;
import org.junit.Test;

public class PropLoaderTests extends BaseTest {
	
	public interface ConfigLoader {
		String load(String propertyName);
	}
	
	public static class ConfigFieldDescriptor implements FieldDescriptor {
		private String name;
		
		public ConfigFieldDescriptor(String name) {
			this.name = name;
		}
		
		@Override
		public String getName() {
			return name;
		}
	}
	
	public class PropLoaderService implements FieldCopyService {

		protected SimpleLogger logger;
		protected FieldRegistry registry;
		protected BeanUtilsBean beanUtil;
		protected PropertyUtilsBean propertyUtils;
		protected FieldFilter fieldFilter;
		protected BUBeanDetectorService beanDetectorSvc;

		public PropLoaderService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
			this.beanDetectorSvc = new BUBeanDetectorService();

			//customize Date converter.  There is no good defaut for date conversions
			//so well just do yyyy-MM-dd
			DateConverter dateConverter = new DateConverter();
			dateConverter.setPattern("yyyy-MM-dd");
			ConvertUtils.register(dateConverter, Date.class);
		}


		@Override
		public List<FieldPair> buildAutoCopyPairs(TargetPair targetPair) {
			Object sourceObj = targetPair.getSrcObj();
			if (! (sourceObj instanceof ConfigLoader)) {
				String err = String.format("sourceObj is not be a ConfigLoader");
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

		private List<FieldPair> buildAutoCopyPairsNoRegister(Class<? extends Object> class2) {
			//	        final PropertyDescriptor[] arSrc = propertyUtils.getPropertyDescriptors(class1);
			final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(class2);

			List<FieldPair> fieldPairs = new ArrayList<>();
			for (int i = 0; i < arDest.length; i++) {
				PropertyDescriptor pd = arDest[i];
				if (! fieldFilter.shouldProcess(class2, pd.getName())) {
					continue; // No point in trying to set an object's class
				}

				//since we have no way to enumerate all properties we'll
				//autocopy all of class2's fields
				//TODO: maybe do nothing here. autocopy doesn't really make sense
				FieldPair pair = new FieldPair();
				pair.srcProp = new ConfigFieldDescriptor(pd.getName());
				pair.destFieldName = pd.getName();
				pair.destProp = new BeanUtilsFieldDescriptor(pd);
				fieldPairs.add(pair);
			}
			return fieldPairs;
		}

		protected void validateIsAllowed(FieldPair pair) throws NoSuchMethodException, InstantiationException, IllegalAccessException {
			Class<?> destClass = isNotAllowed(pair);
			if (destClass != null) {
				BeanUtilsFieldDescriptor fd1 = (BeanUtilsFieldDescriptor) pair.srcProp;
				String err = String.format("Not allowed to copy %s to %s", fd1.pd.getPropertyType().getName(), destClass.getName());
				throw new FieldCopyException(err);
			}
		}

		/**
		 * Additional rules that we want to enforce.
		 * For example, not allowed to copy an int to a boolean.
		 * 
		 * @return null if allowed, else class that we are NOT allowed to copy to
		 */
		private Class<?> isNotAllowed(FieldPair pair) {
			Class<?> type = pair.getDestClass();

			if (Number.class.isAssignableFrom(type) || isNumberPrimitive(type)) {
				return null;
			} else if (Date.class.isAssignableFrom(type)) {
				return null;
			} else if (type.isEnum()) {
				return null;
			} else if (Collection.class.isAssignableFrom(type)) {
				return null; //todo: do we support lists?
			} else if (type.isArray()) {
				return null;
			} else {
				return type; //not allowed
			}
		}
		private boolean isNumberPrimitive(Class<?> srcType) {
			if (Integer.TYPE.equals(srcType) ||
					Long.TYPE.equals(srcType) ||
					Double.TYPE.equals(srcType) ||
					Float.TYPE.equals(srcType) ||
					Short.TYPE.equals(srcType)) {
				return true;
			}
			return false;
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
		public String generateExecutionPlanCacheKey(CopySpec spec) {
			//NOTE. the following key will not work if you have multiple conversions of the
			//same pair of source,destObj but with different fields, mappings, and converters.
			//If that is the case, you MUST key cacheKey and provide a unique value.

			//if source or destObj are null we will catch it during copy
			String class1Name = spec.sourceObj == null ? "" : spec.sourceObj.getClass().getName();
			String class2Name = spec.destObj == null ? "" : spec.destObj.getClass().getName();
			return String.format("%s--%s", class1Name, class2Name);
		}

		protected Object getLoggableString(Object value) {
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

		@Override
		public void copyFields(CopySpec copySpec) {
			Object srcObj = copySpec.sourceObj;
			Object destObj = copySpec.destObj;

			for(FieldPair pair: copySpec.fieldPairs) {
				final FieldDescriptor origDescriptor = pair.srcProp;
				final String name = origDescriptor.getName();
				//				state.currentFieldName = name; //for logging errors

				//check for readability and writability
				//				if (! propertyUtils.isReadable(srcObj, name)) {
				//					String error = String.format("Source Field '%s' is not readable", name);
				//					throw new FieldCopyException(error);
				//				}
				if (!propertyUtils.isWriteable(destObj, pair.destFieldName)) {
					String error = String.format("Destination Field '%s' is not writeable", name);
					throw new FieldCopyException(error);
				}				
				fillInDestPropIfNeeded(pair, destObj.getClass());

				//	            validateIsAllowed(pair);

				//handle list, array, list to array, and viceversa
				//	            ValueConverter converter = findOrCreateCollectionConverter(fieldPlan, pair, classPlan);
				//add converter if one matches
				//	        		if (converter == null) {
				//	        			converter = converterSvc.findConverter(copySpec, pair, srcObj, copySpec.converterL);
				//	        		}

				//val = findProperty ....
				ConfigLoader loader = (ConfigLoader) copySpec.sourceObj;
				String pname = "name";
				String value = loader.load(pname);
				//apply converter!!!
				//store in destObj
				String destFieldName = pair.destFieldName;
				try {
					beanUtil.copyProperty(copySpec.destObj, destFieldName, value);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
		public void addBuiltInConverter(ValueConverter converter) {
			// TODO Auto-generated method stub
		}
		@Override
		public void setMetrics(CopyMetrics metrics) {
			// TODO Auto-generated method stub
		}
		@Override
		public CopyMetrics getMetrics() {
			// TODO Auto-generated method stub
			return null;
		}
	}	
	
	
	public static class MyLoader implements ConfigLoader {

		@Override
		public String load(String propertyName) {
			return "bob";
		}
	}
	public class MyFactory implements CopierFactory {
		public FieldCopyService copySvc;
		
		public MyFactory(FieldCopyService copySvc) {
			this.copySvc = copySvc;
		}
		
		@Override
		public FieldCopier createCopier() {
			return new FieldCopier(copySvc);
		}
	}

	@Test
	public void test() {
		MyLoader loader = new MyLoader();
		Dest dest = new Dest(null, null);
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).field("name", "name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(null, dest.getTitle());
	}

	private FieldCopier createConfigCopier() {
		FieldRegistry registry = new FieldRegistry();
		DefaultFieldFilter filter = new DefaultFieldFilter();
		SimpleConsoleLogger logger = new SimpleConsoleLogger();
		PropLoaderService copySvc = new PropLoaderService(logger, registry, filter);
		MyFactory factory = new MyFactory(copySvc);
		return factory.createCopier();
	}
}
