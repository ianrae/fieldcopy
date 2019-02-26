package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.junit.Test;


public class BeanUtilTests {
	public static class FieldOptions {
		public boolean printStackTrace = false;
		public boolean logEachCopy = false;
	}
	
	public static class FieldPair {
		public PropertyDescriptor srcDesc;
		public String destFieldName;
	}
	
	public static class FieldRegistry {
		private ConcurrentHashMap<String,List<FieldPair>> autocopyCache = new ConcurrentHashMap<>();
		
		public FieldRegistry() {
		}
		public List<FieldPair> findAutoCopyInfo(Class<?> clazz1, Class<?> clazz2) {
			String key = buildClassPairKey(clazz1, clazz2);
			return autocopyCache.get(key);
		}
		public void registerAutoCopyInfo(Class<?> clazz1, Class<?> clazz2, List<FieldPair> fieldPairs) {
			String key = buildClassPairKey(clazz1, clazz2);
			autocopyCache.put(key, fieldPairs);
		}
		private String buildClassPairKey(Class<?> class1, Class<?> class2) {
			return String.format("%s--%s", class1.getName(), class2.getName());
		}
	}
	
	
	public static class FieldCopyService {
		private SimpleLogger logger;
		private BeanUtilsBean beanUtil;
		private PropertyUtilsBean propertyUtils;
		private FieldRegistry registry;
		
		public FieldCopyService(SimpleLogger logger, FieldRegistry registry) {
			this.logger = logger;
			this.registry = registry;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
		}

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
                if ("class".equals(pd.getName())) {
                	continue; // No point in trying to set an object's class
                }

            	String targetFieldName = findMatchingField(arDest, pd.getName());
            	
            	FieldPair pair = new FieldPair();
            	pair.srcDesc = pd;
            	pair.destFieldName = targetFieldName;
            	fieldPairs.add(pair);
            }
			
			registry.registerAutoCopyInfo(sourceObj.getClass(), destObj.getClass(), fieldPairs);
            return fieldPairs;
		}
		
		public void copyFields(Object sourceObj, Object destObj, List<FieldPair> fieldPairs)  {
			try {
				doCopyFields(sourceObj, destObj, fieldPairs);
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

		private void doCopyFields(Object sourceObj, Object destObj, List<FieldPair> fieldPairs) throws IllegalAccessException, InvocationTargetException {
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
                final PropertyDescriptor origDescriptor = pair.srcDesc;
                final String name = origDescriptor.getName();
                if ("class".equals(name)) {
                	continue; // No point in trying to set an object's class
                }
                if (propertyUtils.isReadable(orig, name) &&
                		propertyUtils.isWriteable(dest, name)) {
                	try {
                		final Object value =
                				propertyUtils.getSimpleProperty(orig, name);
                		beanUtil.copyProperty(dest, pair.destFieldName, value);
                	} catch (final NoSuchMethodException e) {
                		// Should not happen
                	}
                }
			}
		}
		
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

		public void copyFieldsByName(Object sourceObj, Object destObj, List<String> srcList, List<String> destList) {
            final PropertyDescriptor[] arSrc = propertyUtils.getPropertyDescriptors(sourceObj);
            final PropertyDescriptor[] arDest = propertyUtils.getPropertyDescriptors(destObj);
    		
            List<FieldPair> fieldPairs = new ArrayList<>();
            for(int i = 0; i < srcList.size(); i++) {
            	String srcField = srcList.get(i);
            	String destField = destList.get(i);
            	PropertyDescriptor srcProp = findField(arSrc, srcField);
            	PropertyDescriptor destProp = findField(arDest, destField);
            	
            	FieldPair pair = new FieldPair();
            	pair.srcDesc = srcProp;
            	pair.destFieldName = destProp.getName();
            	fieldPairs.add(pair);
            }
            
            copyFields(sourceObj, destObj, fieldPairs);
		}

		private PropertyDescriptor findField(PropertyDescriptor[] arSrc, String fieldName) {
			for(PropertyDescriptor pd: arSrc) {
				if (pd.getName().equals(fieldName)) {
					return pd;
				}
			}
			return null;
		}

		public SimpleLogger getLogger() {
			return logger;
		}

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
	
	
	//fluent api
	public static class FCB2 {
		private FieldCopier root;
		private List<String> srcList = new ArrayList<>();
		private List<String> destList = new ArrayList<>();

		public FCB2(FieldCopier fieldCopierBuilder, String srcField, String destField) {
			this.root = fieldCopierBuilder;
			srcList.add(srcField);
			destList.add(destField);
		}
		
		public FCB2 copyField(String srcFieldName, String destFieldName) {
			srcList.add(srcFieldName);
			destList.add(destFieldName);
			return this;
		}
		
		public void execute() {
			FieldCopyService fieldCopier = root.createFieldCopier();
			fieldCopier.copyFieldsByName(root.sourceObj, root.destObj, srcList, destList);
		}
	}
	public static class FCB1 {

		private FieldCopier root;
		private List<String> excludeList = new ArrayList<>();
		private boolean doAutoCopy;

		public FCB1(FieldCopier fieldCopierBuilder) {
			this.root = fieldCopierBuilder;
		}
		
		public FCB1 exclude(String...fieldNames) {
			this.excludeList = Arrays.asList(fieldNames);
			return this;
		}
		public FCB1 exclude(Value...fields) {

			List<String> list = new ArrayList<>();
			for(Value val: fields) {
				list.add(val.name());
			}
			this.excludeList = list;
			return this;
		}
		
//		x.copy(s,t).autoCopy().execute();
//		x.copy(s,t).include(...).execute();
		
		public FCB1 autoCopy() {
			this.doAutoCopy = true;
			return this;
		}
		
		public void execute() {
			if (this.doAutoCopy) {
				List<FieldPair> fieldPairs = root.copier.buildAutoCopyPairs(root.sourceObj.getClass(), root.destObj.getClass());
				
				List<FieldPair> fieldsToCopy = new ArrayList<>();
				for(FieldPair pair: fieldPairs) {
					if (excludeList.contains(pair.srcDesc.getName())) {
						continue;
					}
					
					fieldsToCopy.add(pair);
				}
				
				FieldCopyService fieldCopier = root.createFieldCopier();
				fieldCopier.copyFields(root.sourceObj, root.destObj, fieldPairs);
			}
		}
		
		public FCB2 copyField(String srcFieldName, String destFieldName) {
			return new FCB2(root, srcFieldName, destFieldName);
		}
		public FCB2 copyField(Value srcField, Value destField) {
			return new FCB2(root, srcField.name(), destField.name());
		}
	}


	public static class FieldCopier {
		private static FieldCopier singleton;
		
		private SimpleLogger logger;
		private FieldRegistry registry;
		private FieldCopyService copier;
		Object sourceObj;
		Object destObj;

		public FieldCopier(FieldRegistry registry, FieldCopyService copier, SimpleLogger logger) {
			this.logger = logger;
			this.registry = registry;
			this.copier = copier;
		}
		
		//TODO: make thread-safe
		public static FieldCopier builder() {
			if (singleton == null) {
				SimpleLogger logger = new SimpleConsoleLogger();
				FieldRegistry reg = new FieldRegistry();
				FieldCopyService copier = new FieldCopyService(logger, reg);
				singleton = new FieldCopier(reg, copier, logger);
			}
			return singleton;
		}
		
		public FCB1 copy(Object sourceObj, Object destObj) {
			this.sourceObj = sourceObj;
			this.destObj = destObj;
			return new FCB1(this);
		}
		
		FieldCopyService createFieldCopier() {
			return copier;
		}
	}	
	
	public interface CopyFactory {
		SimpleLogger createLogger();
		FieldCopyService createCopyService();
		FieldCopier createCopier();
	}
	
	public static class DefaultCopyFactory implements CopyFactory {
		private static DefaultCopyFactory theSingleton;
		
		public static CopyFactory Builder() {
			if (theSingleton == null) {
				theSingleton = new DefaultCopyFactory();
			}
			return theSingleton;
		}

		@Override
		public SimpleLogger createLogger() {
			return new SimpleConsoleLogger();
		}

		@Override
		public FieldCopyService createCopyService() {
			SimpleLogger logger = createLogger();
			FieldRegistry registry = new FieldRegistry();
			FieldCopyService copySvc = new FieldCopyService(logger, registry);
			return copySvc;
		}

		@Override
		public FieldCopier createCopier() {
			FieldCopyService copySvc = createCopyService();
			FieldCopier builder = new FieldCopier(copySvc.getRegistry(), copySvc, copySvc.getLogger());
			return builder;
		}
	}
	
	
	public static class Source {
		private String name;
		private int age;

		public Source(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}
	public static class Dest {
		private String name;
		private int age;

		public Dest(String name, int age) {
			this.name = name;
			this.age = age;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public int getAge() {
			return age;
		}

		public void setAge(int age) {
			this.age = age;
		}
	}
	
	@Test
	public void test() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);
		
		FieldCopyService copySvc = createCopyService(); 
		List<FieldPair> fieldPairs = copySvc.buildAutoCopyPairs(src, dest);
		copySvc.copyFields(src, dest, fieldPairs);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
		
		copySvc.dumpFields(src);
	}
	
	@Test
	public void testAutoCopyCache() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);
		
		FieldCopyService copySvc = createCopyService(); 
		List<FieldPair> fieldPairs = copySvc.buildAutoCopyPairs(src, dest);
		List<FieldPair> fieldPairs2 = copySvc.buildAutoCopyPairs(src, dest);
		
		assertSame(fieldPairs, fieldPairs2);
	}
	
	@Test
	public void testCopyByName() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).copyField("name", "name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(-1, dest.getAge());
		
		dest = new Dest(null, -1);
		copier.copy(src, dest).copyField("age", "age").execute();
		assertEquals(null, dest.getName());
		assertEquals(33, dest.getAge());
		
		dest = new Dest(null, -1);
		copier.copy(src, dest).copyField("age", "age").copyField("name", "name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
	
	//--
	private FieldCopyService createCopyService() {
		SimpleLogger logger = new SimpleConsoleLogger();
		FieldRegistry registry = new FieldRegistry();
		FieldCopyService copySvc = new FieldCopyService(logger, registry);
		return copySvc;
	}
	
	private FieldCopier createCopier() {
		SimpleLogger logger = new SimpleConsoleLogger();
		FieldRegistry registry = new FieldRegistry();
		FieldCopyService copySvc = new FieldCopyService(logger, registry);
		FieldCopier builder = new FieldCopier(registry, copySvc, logger);
		return builder;
	}


}
