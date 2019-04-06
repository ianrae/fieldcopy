package org.dnal.fieldcopy.proploader;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.CopierFactory;
import org.dnal.fieldcopy.DefaultValueTests.Dest;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.TransitiveTests.MyConverter1;
import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyException;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldCopyUtils;
import org.dnal.fieldcopy.core.FieldDescriptor;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.core.TargetPair;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.metrics.CopyMetrics;
import org.dnal.fieldcopy.metrics.DoNothingMetrics;
import org.dnal.fieldcopy.service.beanutils.BUBeanDetectorService;
import org.dnal.fieldcopy.service.beanutils.BUConverterService;
import org.dnal.fieldcopy.service.beanutils.BUHelperService;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;
import org.dnal.fieldcopy.util.ThreadSafeList;
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
		private BUConverterService converterSvc;
		private CopyMetrics metrics = new DoNothingMetrics();

		public PropLoaderService(SimpleLogger logger, FieldRegistry registry, FieldFilter fieldFilter) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
			this.beanDetectorSvc = new BUBeanDetectorService();
			this.helperSvc = new BUHelperService(logger);
			this.converterSvc = new BUConverterService(logger, this.beanDetectorSvc, this);
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
		public FieldDescriptor resolveSourceField(String srcField, TargetPair targetPair) {
			return new ConfigFieldDescriptor(srcField);
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
				

				boolean hasSetterMethod = propertyUtils.isWriteable(destObj, pair.destFieldName);
				fillInDestPropIfNeeded(pair, destObj.getClass());
				
				//	            validateIsAllowed(pair);

				ValueConverter converter = findAConverter(copySpec, pair);

				//val = findProperty ....
				ConfigLoader loader = (ConfigLoader) copySpec.sourceObj;
				String pname = pair.srcProp.getName();
				String value = loader.load(pname);
				if (value == null && pair.defaultValue != null) {
					value = pair.defaultValue.toString();
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
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						Field field = FieldUtils.getField(copySpec.destObj.getClass(), destFieldName);
						value = convertForField(field, value);
						FieldUtils.writeField(copySpec.destObj, destFieldName, value, true);
					} catch (Exception ex) {
						String err = String.format(ex.getMessage());
						throw new FieldCopyException(err, ex);
					}
				}
			}
		}
		
		private String convertForField(Field field, String value) {
//			if (field.getType())
			// TODO Auto-generated method stub
			return null;
		}

		private void addConverterAndMappingLists(ConverterContext ctx, CopySpec copySpec) {
			//mappings are not supported
//			if (CollectionUtils.isNotEmpty(copySpec.mappingL)) {
//				ctx.mappingL = new ArrayList<>();
//				ctx.mappingL.addAll(copySpec.mappingL);
//			}
			if (ThreadSafeList.isNotEmpty(copySpec.converterL)) {
				ctx.converterL = new ArrayList<>();
				copySpec.converterL.addIntoOtherList(ctx.converterL);
//				ctx.converterL.addAll(execPlan.copySpec.converterL);
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
	
	
	public static class MyLoader implements ConfigLoader {

		@Override
		public String load(String propertyName) {
			switch(propertyName) {
			case "name":
				return "bob";
			case "title":
				return "Mr";
			case "app.port":
				return "3000";
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
	
	public static class Dest2 {
		private String name;
		private String title;
		private int port;
		
		public Dest2() {
		}
		public Dest2(String name, String title) {
			this.name = name;
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public int getPort() {
			return port;
		}
		public void setPort(int port) {
			this.port = port;
		}
	}
	
	public static class MyPrivateFields {
		private String name;
		private String title;
		private int port;
		
		public MyPrivateFields(MyLoader loader, FieldCopier copier) {
			copier.copy(loader, this).field("name").field("title").field("app.port", "port").execute();
		}
		
		public void init() {
			
		}

		public String getName() {
			return name;
		}
		public String getTitle() {
			return title;
		}
		public int getPort() {
			return port;
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
	@Test
	public void test2a() {
		MyLoader loader = new MyLoader();
		Dest dest = new Dest(null, null);
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).field("app.port", "name").execute();
		assertEquals("3000", dest.getName());
		assertEquals(null, dest.getTitle());
	}
	@Test
	public void test3() {
		MyLoader loader = new MyLoader();
		Dest2 dest = new Dest2(null, null);
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).field("nosuchname", "port").defaultValue(3000).execute();
		assertEquals(3000, dest.getPort());
	}
	@Test
	public void test4() {
		MyLoader loader = new MyLoader();
		Dest2 dest = new Dest2(null, null);
		
		MyConverter1 conv = new MyConverter1();
		
		FieldCopier copier = createConfigCopier();
		copier.copy(loader, dest).withConverters(conv).field("name").execute();
		assertEquals("BOB", dest.getName());
	}
	@Test
	public void testFieldWrite() {
		MyLoader loader = new MyLoader();
		Dest destx = new Dest();
		try {
			FieldUtils.writeField(destx, "name", "bill", true);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("bill", destx.getName());
	}
	@Test
	public void test5() {
		MyLoader loader = new MyLoader();

		FieldCopier copier = createConfigCopier();
		MyPrivateFields dest = new MyPrivateFields(loader, copier);
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
