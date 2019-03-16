package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.BeanUtilTests.Dest;
import org.dnal.fieldcopy.BeanUtilTests.Source;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;


public class DeeplyNestedListTests {
	
	public static class Taxi {
		private int width;
		private List<List<List<Integer>>> nestedSizes;
		private List<List<List<Source>>> nestedSources;
		
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			this.width = width;
		}
		public List<List<List<Integer>>> getNestedSizes() {
			return nestedSizes;
		}
		public void setNestedSizes(List<List<List<Integer>>> nestedSizes) {
			this.nestedSizes = nestedSizes;
		}
		public List<List<List<Source>>> getNestedSources() {
			return nestedSources;
		}
		public void setNestedSources(List<List<List<Source>>> nestedSources) {
			this.nestedSources = nestedSources;
		}
	}
	public static class TaxiDTO {
		private int width;
		private List<List<List<Integer>>> nestedSizes;
		private List<List<List<Dest>>> nestedSources;

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public List<List<List<Integer>>> getNestedSizes() {
			return nestedSizes;
		}

		public void setNestedSizes(List<List<List<Integer>>> nestedSizes) {
			this.nestedSizes = nestedSizes;
		}

		public List<List<List<Dest>>> getNestedSources() {
			return nestedSources;
		}

		public void setNestedSources(List<List<List<Dest>>> nestedSources) {
			this.nestedSources = nestedSources;
		}
	}
	
	
	@Test
	public void testNestedInteger() {
		Taxi taxi = createTaxi();
		TaxiDTO dto = new TaxiDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(taxi, dto).autoCopy().execute();
		assertEquals(55, dto.getWidth());
		assertEquals(2, dto.getNestedSizes().size());
		
		List<List<Integer>> list2 = dto.getNestedSizes().get(0);
		chkIntListValue(3, 100, 101, list2.get(0));
		chkIntListValue(3, 110, 111, list2.get(1));
		list2 = dto.getNestedSizes().get(1);
		chkIntListValue(3, 200, 201, list2.get(0));
		chkIntListValue(3, 210, 211, list2.get(1));
	}
	
	
	private Taxi createTaxi() {
		Taxi taxi = new Taxi();
		taxi.setWidth(55);
		
		List<Integer> list1A = Arrays.asList(100,101,102);
		List<Integer> list1B = Arrays.asList(110,111,112);
		List<Integer> list2A = Arrays.asList(200,201,202);
		List<Integer> list2B = Arrays.asList(210,211,212);
		
		List<List<List<Integer>>> list3 = new ArrayList<>();
		List<List<Integer>> list2 = new ArrayList<>();
		list2.add(list1A);
		list2.add(list1B);
		list3.add(list2);
		
		list2 = new ArrayList<>();
		list2.add(list2A);
		list2.add(list2B);
		list3.add(list2);
		
		taxi.setNestedSizes(list3);
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
	private void log(String s) {
		System.out.println(s);
	}
}
