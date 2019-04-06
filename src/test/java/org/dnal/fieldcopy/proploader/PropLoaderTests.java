package org.dnal.fieldcopy.proploader;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import org.dnal.fieldcopy.service.beanutils.BUHelperService;
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
		protected BUHelperService helperSvc;

		public PropLoaderService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
			this.beanDetectorSvc = new BUBeanDetectorService();
			this.helperSvc = new BUHelperService(logger);
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

		@Override
		public void copyFields(CopySpec copySpec) {
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
				String pname = pair.srcProp.getName();
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
			T destObj = (T) helperSvc.createObject(destClass);
			copySpec.destObj = destObj;
			copyFields(copySpec);
			return destObj;
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
			switch(propertyName) {
			case "name":
				return "bob";
			case "title":
				return "Mr";
			default:
				return null;
			}
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
	@Test
	public void test2() {
		MyLoader loader = new MyLoader();
		Dest dest = new Dest(null, null);
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).field("name", "name").field("title").execute();
		assertEquals("bob", dest.getName());
		assertEquals("Mr", dest.getTitle());
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
