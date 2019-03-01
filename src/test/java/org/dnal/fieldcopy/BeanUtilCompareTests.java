package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.dnal.fc.CopyOptions;
import org.dnal.fc.DefaultCopyFactory;
import org.dnal.fc.FieldCopier;
import org.dnal.fc.beanutils.BeanUtilFieldCopyService;
import org.dnal.fc.core.AutoCopyFieldFilter;
import org.dnal.fc.core.CopyFactory;
import org.dnal.fc.core.DefaultFieldFilter;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldPair;
import org.dnal.fc.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.junit.Test;


public class BeanUtilCompareTests {
	
	public interface FieldCompareService {
		boolean compareFields(Object sourceObj, Object destObj);
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
		private BeanUtilsBean beanUtil;
		private PropertyUtilsBean propertyUtils;

		public BeanUtilFieldCompareService(SimpleLogger logger, AutoCopyFieldFilter fieldFilter) {
			this.logger = logger;
			this.beanUtil =  BeanUtilsBean.getInstance();
			this.propertyUtils =  new PropertyUtilsBean();
			this.fieldFilter = fieldFilter;
		}

		
		@Override
		public boolean compareFields(Object sourceObj, Object destObj) {
			// TODO Auto-generated method stub
			return false;
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
		Dest dest = new Dest(null, -1);
		
		FieldCompareService copySvc = createCompareService(); 
		boolean b = copySvc.compareFields(src, src);
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
