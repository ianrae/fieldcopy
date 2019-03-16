package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.List;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;

public class BeanUtilTests {
	public static class Source {
		private String name;
		private int age;

		public Source() {
		}
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

		public Dest() {
		}
	
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
		
		FieldCopyService copySvc = createCopyService(); 
		List<FieldPair> fieldPairs = copySvc.buildAutoCopyPairs(src.getClass(), dest.getClass());
		
		
		CopySpec spec = new CopySpec();
		spec.sourceObj = src;
		spec.destObj = dest;
		spec.fieldPairs = fieldPairs;
		spec.mappingL = null;
		spec.options = new CopyOptions();
		copySvc.copyFields(spec);
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
		
		copySvc.dumpFields(src);
	}
	
	@Test
	public void testAutoCopyCache() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);
		
		FieldCopyService copySvc = createCopyService(); 
		List<FieldPair> fieldPairs = copySvc.buildAutoCopyPairs(src.getClass(), dest.getClass());
		List<FieldPair> fieldPairs2 = copySvc.buildAutoCopyPairs(src.getClass(), dest.getClass());
		
		assertSame(fieldPairs, fieldPairs2);
	}
	
	@Test
	public void testAutoCopy() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
	@Test
	public void testCopyByName() {
		Source src = new Source("bob", 33);
		Dest dest = new Dest(null, -1);
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).cacheKey("key1").field("name", "name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(-1, dest.getAge());
		
		dest = new Dest(null, -1);
		copier.copy(src, dest).cacheKey("key2").field("age", "age").execute();
		assertEquals(null, dest.getName());
		assertEquals(33, dest.getAge());
		
		dest = new Dest(null, -1);
		copier.copy(src, dest).cacheKey("key3").field("age", "age").field("name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge());
	}
	
	@Test
	public void testCopyByName2() {
		Source src = new Source("bob", 33);
		Dest2 dest = new Dest2(null, -1, null, -1);
		
		FieldCopier copier = createCopier();
		copier.getOptions().logEachCopy = true;
		copier.copy(src, dest).cacheKey("key1").field("name", "name2").execute();
		assertEquals("bob", dest.getName2());
		assertEquals(-1, dest.getAge());
		
		dest = new Dest2(null, -1, null, -1);
		copier.copy(src, dest).cacheKey("key2").field("age", "age2").execute();
		assertEquals(null, dest.getName());
		assertEquals(33, dest.getAge2());
		
		dest = new Dest2(null, -1, null, -1);
		copier.copy(src, dest).cacheKey("key3").field("age", "age2").field("name").execute();
		assertEquals("bob", dest.getName());
		assertEquals(33, dest.getAge2());
	}
	
	
	//--
	private FieldCopyService createCopyService() {
		return DefaultCopyFactory.Factory().createCopyService();
//		SimpleLogger logger = new SimpleConsoleLogger();
//		FieldRegistry registry = new FieldRegistry();
//		FieldCopyService copySvc = new FieldCopyService(logger, registry);
//		return copySvc;
	}
	
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
//		SimpleLogger logger = new SimpleConsoleLogger();
//		FieldRegistry registry = new FieldRegistry();
//		FieldCopyService copySvc = new FieldCopyService(logger, registry);
//		FieldCopier builder = new FieldCopier(registry, copySvc, logger);
//		return builder;
	}


}
