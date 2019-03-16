package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.BeanUtilTests.Dest;
import org.dnal.fieldcopy.BeanUtilTests.Source;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;


public class ListListTests {
	
	public static class Taxi {
		private int width;
		private List<List<Integer>> sizes;
		private List<List<Source>> sources;
		
		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public List<List<Integer>> getSizes() {
			return sizes;
		}

		public void setSizes(List<List<Integer>> sizes) {
			this.sizes = sizes;
		}

		public List<List<Source>> getSources() {
			return sources;
		}

		public void setSources(List<List<Source>> sources) {
			this.sources = sources;
		}
	}
	public static class TaxiDTO {
		private int width;
		private List<List<Integer>> sizes;
		private List<List<Dest>> sources;

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public List<List<Integer>> getSizes() {
			return sizes;
		}

		public void setSizes(List<List<Integer>> sizes) {
			this.sizes = sizes;
		}

		public List<List<Dest>> getSources() {
			return sources;
		}

		public void setSources(List<List<Dest>> sources) {
			this.sources = sources;
		}
	}
	
	
	@Test
	public void testInteger() {
		Taxi taxi = createTaxi();
		TaxiDTO dto = new TaxiDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(taxi, dto).autoCopy().execute();
		assertEquals(55, dto.getWidth());
		assertEquals(2, dto.getSizes().size());
		chkIntListValue(3, 100, 200, dto.getSizes().get(0));
		chkIntListValue(2, 44, 45, dto.getSizes().get(1));
	}
	@Test
	public void testBean() {
		Taxi taxi = createTaxi();
		taxi.sources = new ArrayList<>();
		List<Source> list = new ArrayList<>();
		list.add(new Source("bob", 33));
		list.add(new Source("sue", 44));
		taxi.sources.add(list);
		
		TaxiDTO dto = new TaxiDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(taxi, dto).autoCopy().execute();
		assertEquals(55, dto.getWidth());
		assertEquals(2, dto.getSizes().size());
		
		assertEquals(1, dto.getSources().size());
		List<Dest> list2 = dto.getSources().get(0);
		assertEquals(2, list2.size());
		assertEquals("bob", list2.get(0).getName());
		assertEquals("sue", list2.get(1).getName());
	}
	
	
	private Taxi createTaxi() {
		Taxi taxi = new Taxi();
		taxi.setWidth(55);
		
		List<Integer> list = Arrays.asList(100,200,300);
		List<Integer> list2 = Arrays.asList(44, 45);
		List<List<Integer>> list3 = new ArrayList<>();
		list3.add(list);
		list3.add(list2);
		taxi.setSizes(list3);
		return taxi;
	}
	protected void chkIntListValue(int expected, int n1, int n2, List<Integer> list) {
		assertEquals(expected, list.size());
		
		if (expected > 0) {
			assertEquals(n1, list.get(0).intValue());
		}
		if (expected > 1) {
			assertEquals(n2, list.get(1).intValue());
		}
	}

	//--
	private FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
}
