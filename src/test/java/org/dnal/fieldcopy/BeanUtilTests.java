package org.dnal.fieldcopy;

import static org.junit.Assert.*;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.dnal.fieldcopy.ValueTests.FieldCopyUtils;
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
	
	
	public static class FieldCopier {
		private SimpleLogger logger;
		private BeanUtilsBean beanUtil;
		private PropertyUtilsBean propertyUtils;
		private FieldRegistry registry;
		
		public FieldCopier(SimpleLogger logger, FieldRegistry registry) {
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
		
		FieldCopier copier = buildCopier(); 
		List<FieldPair> fieldPairs = copier.buildAutoCopyPairs(src, dest);
		copier.copyFields(src, dest, fieldPairs);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
		
		copier.dumpFields(src);
	}
	
	@Test
	public void testAutoCopyCache() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);
		
		FieldCopier copier = buildCopier(); 
		List<FieldPair> fieldPairs = copier.buildAutoCopyPairs(src, dest);
		List<FieldPair> fieldPairs2 = copier.buildAutoCopyPairs(src, dest);
		
		assertSame(fieldPairs, fieldPairs2);
	}
	
	//--
	private FieldCopier buildCopier() {
		SimpleLogger logger = new SimpleConsoleLogger();
		FieldRegistry registry = new FieldRegistry();
		FieldCopier copier = new FieldCopier(logger, registry);
		return copier;
	}


}
