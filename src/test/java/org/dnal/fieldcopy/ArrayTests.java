package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;

import org.junit.Test;

public class ArrayTests extends BaseTest {
	
	public static class Home {
		private String[] names;

		public String[] getNames() {
			return names;
		}

		public void setNames(String[] names) {
			this.names = names;
		}

	}
	public static class HomeDTO {
		private String[] names;
		private Integer[] sizes;

		public String[] getNames() {
			return names;
		}

		public void setNames(String[] names) {
			this.names = names;
		}

		public Integer[] getSizes() {
			return sizes;
		}

		public void setSizes(Integer[] sizes) {
			this.sizes = sizes;
		}

	}
	
	
	@Test
	public void test() {
		Home src = new Home();
		String[] ar=  {"a", "b", "c"};
		src.setNames(ar);
		
		HomeDTO dest = new HomeDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).autoCopy().execute();
		assertEquals(3, dest.getNames().length);
		assertEquals("a", dest.getNames()[0]);
		assertEquals("c", dest.getNames()[2]);
	}
	
	@Test
	public void testToInteger() {
		Home src = new Home();
		String[] ar=  {"33", "34", "35"};
		src.setNames(ar);
		
		HomeDTO dest = new HomeDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(src, dest).field("names", "sizes").execute();
		assertEquals(3, dest.getSizes().length);
		assertEquals("a", dest.getNames()[0]);
		assertEquals("c", dest.getNames()[2]);
	}
	
	@Test
	public void test3() {
		String[] ar=  {"33", "34", "35"};
		
		Object obj = ar;
		assertEquals(true, obj.getClass().isArray());
		//http://tutorials.jenkov.com/java-reflection/arrays.html
//		Class<?> elClass = String.class;
		Class<?> elClass = obj.getClass().getComponentType();
		assertEquals(String.class, elClass);
		
		String s = (String) Array.get(obj, 1);
		assertEquals("34", s);
	}
	
	
	//--
}
