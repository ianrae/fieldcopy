package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

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

	
	public static class FieldCopier {
		private SimpleLogger logger;

		public FieldCopier(SimpleLogger logger) {
			this.logger = logger;
		}

		public void copyFields(Object sourceObj, Object destObj)  {
			try {
				doCopyFields(sourceObj, destObj);
			} catch (Exception e) {
				throw new FieldCopyException(e.getMessage());
			}
		}
		
		
		private void doCopyFields(Object sourceObj, Object destObj) throws IllegalAccessException, InvocationTargetException {
			if (sourceObj == null) {
				String error = String.format("copyFields. NULL passed to sourceObj");
				throw new FieldCopyException(error);
			}
			if (destObj == null) {
				String error = String.format("copyFields. NULL passed to destObj.");
				throw new FieldCopyException(error);
			}
			
			BeanUtilsBean beanUtil =  BeanUtilsBean.getInstance();
			PropertyUtilsBean propertyUtils =  new PropertyUtilsBean();
			Object orig = sourceObj;
			Object dest = destObj;
			
            final PropertyDescriptor[] origDescriptors =
            		propertyUtils.getPropertyDescriptors(orig);
                for (final PropertyDescriptor origDescriptor : origDescriptors) {
                    final String name = origDescriptor.getName();
                    if ("class".equals(name)) {
                        continue; // No point in trying to set an object's class
                    }
                    if (propertyUtils.isReadable(orig, name) &&
                    		propertyUtils.isWriteable(dest, name)) {
                        try {
                            final Object value =
                            		propertyUtils.getSimpleProperty(orig, name);
                            beanUtil.copyProperty(dest, name, value);
                        } catch (final NoSuchMethodException e) {
                            // Should not happen
                        }
                    }
                }
		}
		
		public void dumpFields(Object sourceObj) {
			try {
				BeanUtilsBean beanUtil =  BeanUtilsBean.getInstance();
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
		
		SimpleLogger logger = new SimpleConsoleLogger();
		FieldCopier copier = new FieldCopier(logger);
		
		copier.copyFields(src, dest);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
		
		copier.dumpFields(src);
	}

}
