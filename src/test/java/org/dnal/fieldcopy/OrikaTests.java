package org.dnal.fieldcopy;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import ma.glasnost.orika.BoundMapperFacade;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

//https://www.baeldung.com/orika-mapping
public class OrikaTests {

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
		mapperFactory.classMap(Source.class, Dest.class);
		MapperFacade mapper = mapperFactory.getMapperFacade();
		Source src = new Source("Baeldung", 10);
		Dest dest = mapper.map(src, Dest.class);

		assertEquals(dest.getAge(), src.getAge());
		assertEquals(dest.getName(), src.getName());	
	}

	@Test
	public void givenSrcAndDest_whenMapsUsingBoundMapper_thenCorrect() {
		BoundMapperFacade<Source, Dest> 
		boundMapper = mapperFactory.getMapperFacade(Source.class, Dest.class);
		Source src = new Source("baeldung", 10);
		Dest dest = boundMapper.map(src);

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
