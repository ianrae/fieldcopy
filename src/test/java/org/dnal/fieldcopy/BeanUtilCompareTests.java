package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.beans.PropertyDescriptor;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.dnal.fc.core.AutoCopyFieldFilter;
import org.dnal.fc.core.DefaultFieldFilter;
import org.dnal.fc.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.junit.Test;


public class BeanUtilCompareTests {
	
	public static enum CompareMode {
		A_CONTAINS_B, //foreach field in B, its field in A is the same
		B_CONTAINS_A, //for each field in A, its field in B is the same
		BOTH
	}
	
	public interface FieldCompareService {
		boolean compareFields(Object sourceObj, Object destObj, CompareMode mode);
		SimpleLogger getLogger();
	}	
	
	public interface CompareFactory {
		SimpleLogger createLogger();
		AutoCopyFieldFilter createAutoCopyFieldFilter();
		FieldCompareService createCompareService();
//		FieldComparer createComparer();
	}	
	
	public static class BeanUtilFieldCompareService implements FieldCompareService {

		private SimpleLogger logger;
		private AutoCopyFieldFilter fieldFilter;
		private PropertyUtilsBean propertyUtils;

		public BeanUtilFieldCompareService(SimpleLogger logger, AutoCopyFieldFilter fieldFilter) {
			this.logger = logger;
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
		}

		
		@Override
		public boolean compareFields(Object objA, Object objB, CompareMode mode) {
            final PropertyDescriptor[] arA = propertyUtils.getPropertyDescriptors(objA);
            final PropertyDescriptor[] arB = propertyUtils.getPropertyDescriptors(objB);
    		
            for (int i = 0; i < arA.length; i++) {
            	PropertyDescriptor pdA = arA[i];
            	String fieldName = pdA.getName();
            	
            	if (! fieldFilter.shouldCopy(objA, fieldName)) {
            		continue; // No point in trying to set an object's class
                }
            	
            	PropertyDescriptor pdB = findProp(arB, fieldName);
            	if (pdB == null) {
            		throw new RuntimeException("XXXXX");
            	}
            	
                if (propertyUtils.isReadable(objA, fieldName) && propertyUtils.isReadable(objB, fieldName)) {
                	try {
						return doCompareFields(objA, objB, pdA, pdB, fieldName);
					} catch (Exception e) {
						e.printStackTrace();
					}
                }
			}
            
			
			return true;
		}
		
		private PropertyDescriptor findProp(PropertyDescriptor[] arB, String fieldName) {
			for(int i = 0; i < arB.length; i++) {
				PropertyDescriptor pd = arB[i];
				if (pd.getName().equals(fieldName)) {
					return pd;
				}
			}
			return null;
		}


		private boolean doCompareFields(Object sourceObj, Object destObj, PropertyDescriptor pdA, PropertyDescriptor pdB, String fieldName) throws Exception {
			if (propertyUtils.isReadable(sourceObj, fieldName) && propertyUtils.isReadable(destObj, fieldName)) {
				try {
					final Object valueA = propertyUtils.getSimpleProperty(sourceObj, fieldName);
					final Object valueB = propertyUtils.getSimpleProperty(destObj, fieldName);

					if (valueA == null && valueB == null) {
					} else if (valueA == null) {
						return false; //B not null
					} else {
						boolean b = valueA.equals(valueB);
						if (! b) {
							return false;
						}
					}

				} catch (final NoSuchMethodException e) {
					// Should not happen
				}
			}
			return true;
		}

		@Override
		public SimpleLogger getLogger() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	public static class DefaultCompareFactory implements CompareFactory {
		private static DefaultCompareFactory theSingleton;
		private static SimpleLogger theLogger;
		
		public static CompareFactory Factory() {
			if (theSingleton == null) {
				theSingleton = new DefaultCompareFactory();
			}
			return theSingleton;
		}

		@Override
		public SimpleLogger createLogger() {
			if (theLogger != null) {
				return theLogger;
			}
			return new SimpleConsoleLogger();
		}

		@Override
		public FieldCompareService createCompareService() {
			SimpleLogger logger = createLogger();
			FieldRegistry registry = new FieldRegistry();
			AutoCopyFieldFilter fieldFilter = createAutoCopyFieldFilter();
			FieldCompareService copySvc = new BeanUtilFieldCompareService(logger, fieldFilter);
			return copySvc;
		}

//		@Override
//		public FieldComparer createComparer() {
//			FieldCopyService copySvc = createCopyService();
//			FieldCopier builder = new FieldCopier(copySvc);
//			return builder;
//		}
		
		public static void setLogger(SimpleLogger logger) {
			theLogger = logger;
		}

		@Override
		public AutoCopyFieldFilter createAutoCopyFieldFilter() {
			return new DefaultFieldFilter();
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
	public static class Dest2 {
		private String name;
		private int age;
		private String name2;
		private int age2;

		public Dest2(String name, int age, String name2, int age2) {
			this.name = name;
			this.age = age;
			this.name2 = name2;
			this.age2 = age2;
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

		public String getName2() {
			return name2;
		}

		public void setName2(String name2) {
			this.name2 = name2;
		}

		public int getAge2() {
			return age2;
		}

		public void setAge2(int age2) {
			this.age2 = age2;
		}
	}
	
	@Test
	public void test() {
		Source src = new Source("bob", 33);
		Source src2 = new Source("bobx", 33);
		
		FieldCompareService copySvc = createCompareService(); 
		boolean b = copySvc.compareFields(src, src, CompareMode.B_CONTAINS_A);
		assertEquals(true, b);
		
		b = copySvc.compareFields(src, src2, CompareMode.BOTH);
		assertEquals(false, b);
	}
	
//	@Test
//	public void testAutoCopyCache() {
//		Source src = new Source("bob", 33);
//		Dest dest = new Dest(null, -1);
//		
//		FieldCopyService copySvc = createCopyService(); 
//		List<FieldPair> fieldPairs = copySvc.buildAutoCopyPairs(src, dest);
//		List<FieldPair> fieldPairs2 = copySvc.buildAutoCopyPairs(src, dest);
//		
//		assertSame(fieldPairs, fieldPairs2);
//	}
//	
//	@Test
//	public void testAutoCopy() {
//		Source src = new Source("bob", 33);
//		Dest dest = new Dest(null, -1);
//		
//		FieldCopier copier = createCopier();
//		copier.copy(src, dest).autoCopy().execute();
//		assertEquals("bob", dest.getName());
//		assertEquals(33, dest.getAge());
//	}
//	
//	@Test
//	public void testCopyByName() {
//		Source src = new Source("bob", 33);
//		Dest dest = new Dest(null, -1);
//		
//		FieldCopier copier = createCopier();
//		copier.copy(src, dest).field("name", "name").execute();
//		assertEquals("bob", dest.getName());
//		assertEquals(-1, dest.getAge());
//		
//		dest = new Dest(null, -1);
//		copier.copy(src, dest).field("age", "age").execute();
//		assertEquals(null, dest.getName());
//		assertEquals(33, dest.getAge());
//		
//		dest = new Dest(null, -1);
//		copier.copy(src, dest).field("age", "age").field("name").execute();
//		assertEquals("bob", dest.getName());
//		assertEquals(33, dest.getAge());
//	}
//	
//	@Test
//	public void testCopyByName2() {
//		Source src = new Source("bob", 33);
//		Dest2 dest = new Dest2(null, -1, null, -1);
//		
//		FieldCopier copier = createCopier();
//		copier.getOptions().logEachCopy = true;
//		copier.copy(src, dest).field("name", "name2").execute();
//		assertEquals("bob", dest.getName2());
//		assertEquals(-1, dest.getAge());
//		
//		dest = new Dest2(null, -1, null, -1);
//		copier.copy(src, dest).field("age", "age2").execute();
//		assertEquals(null, dest.getName());
//		assertEquals(33, dest.getAge2());
//		
//		dest = new Dest2(null, -1, null, -1);
//		copier.copy(src, dest).field("age", "age2").field("name").execute();
//		assertEquals("bob", dest.getName());
//		assertEquals(33, dest.getAge2());
//	}
//	
	//--
	private FieldCompareService createCompareService() {
//		return DefaultCopyFactory.Factory().createCopyService();
		SimpleLogger logger = new SimpleConsoleLogger();
		FieldCompareService copySvc = new BeanUtilFieldCompareService(logger, new DefaultFieldFilter());
		return copySvc;
	}
	
//	private FieldCopier createCopier() {
//		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
//		return DefaultCopyFactory.Factory().createCopier();
////		SimpleLogger logger = new SimpleConsoleLogger();
////		FieldRegistry registry = new FieldRegistry();
////		FieldCopyService copySvc = new FieldCopyService(logger, registry);
////		FieldCopier builder = new FieldCopier(registry, copySvc, logger);
////		return builder;
//	}


}
