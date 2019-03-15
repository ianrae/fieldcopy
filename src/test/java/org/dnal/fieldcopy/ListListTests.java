package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.junit.Test;


public class ListListTests {
	
	public static class Taxi {
		private int width;
		private List<List<Integer>> sizes;
		
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
	}
	public static class TaxiDTO {
		private int width;
		private List<List<Integer>> sizes;
		
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
	}
	
	
	@Test
	public void test() {
		Taxi taxi = createTaxi();
		TaxiDTO dto = new TaxiDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(taxi, dto).autoCopy().execute();
		assertEquals(55, dto.getWidth());
		assertEquals(2, dto.getSizes().size());
		chkIntListValue(3, 100, 200, dto.getSizes().get(0));
		chkIntListValue(2, 44, 45, dto.getSizes().get(1));
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
