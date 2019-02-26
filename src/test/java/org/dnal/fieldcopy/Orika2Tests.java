package org.dnal.fieldcopy;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

//https://www.baeldung.com/orika-mapping
public class Orika2Tests {

	public static class Source2 {
		private String name;
		private int age;

		public Source2(String name, int age) {
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

		public Dest2(String name, int age) {
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
		mapperFactory.classMap(Source2.class, Dest2.class);
		MapperFacade mapper = mapperFactory.getMapperFacade();
		Source2 src = new Source2("Baeldung", 10);
		Dest2 dest = mapper.map(src, Dest2.class);

		assertEquals(dest.getAge(), src.getAge());
		assertEquals(dest.getName(), src.getName());	
	}

	@Test
	public void givenSrcAndDest_whenMapsUsingBoundMapper_thenCorrect() {
		BoundMapperFacade<Source2, Dest2> 
		boundMapper = mapperFactory.getMapperFacade(Source2.class, Dest2.class);
		Source2 src = new Source2("baeldung", 10);
		Dest2 dest = boundMapper.map(src);

		assertEquals(dest.getAge(), src.getAge());
		assertEquals(dest.getName(), src.getName());
	}	

	//--
	private MapperFactory mapperFactory;

	@Before
	public void init() {
		mapperFactory = new DefaultMapperFactory.Builder().build();

	}
}
