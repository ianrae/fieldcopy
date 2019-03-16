package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.FieldCopierTests.Dest;
import org.dnal.fieldcopy.FieldCopierTests.Source;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.service.beanutils.BeanUtilsFieldDescriptor;
import org.dnal.fieldcopy.service.beanutils.ListSpec;
import org.dnal.fieldcopy.service.beanutils.ReflectionUtil;
import org.junit.Test;


public class ListListTests {
	
	public static class Taxi {
		private int width;
		private List<Integer> sizes;
		private List<List<Integer>> nestedSizes;
		
		private List<Source> sources;
		private List<List<Source>> nestedSources;
		
		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}


		public List<List<Source>> getNestedSources() {
			return nestedSources;
		}

		public void setNestedSources(List<List<Source>> nestedSources) {
			this.nestedSources = nestedSources;
		}

		public List<Source> getSources() {
			return sources;
		}

		public void setSources(List<Source> sources) {
			this.sources = sources;
		}

		public List<List<Integer>> getNestedSizes() {
			return nestedSizes;
		}

		public void setNestedSizes(List<List<Integer>> nestedSizes) {
			this.nestedSizes = nestedSizes;
		}

		public List<Integer> getSizes() {
			return sizes;
		}

		public void setSizes(List<Integer> sizes) {
			this.sizes = sizes;
		}
	}
	public static class TaxiDTO {
		private int width;
		private List<Integer> sizes;
		private List<List<Integer>> nestedSizes;
		private List<Dest> sources;
		private List<List<Dest>> nestedSources;

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public List<List<Dest>> getNestedSources() {
			return nestedSources;
		}

		public void setNestedSources(List<List<Dest>> nestedSources) {
			this.nestedSources = nestedSources;
		}

		public List<Dest> getSources() {
			return sources;
		}

		public void setSources(List<Dest> sources) {
			this.sources = sources;
		}

		public List<List<Integer>> getNestedSizes() {
			return nestedSizes;
		}

		public void setNestedSizes(List<List<Integer>> nestedSizes) {
			this.nestedSizes = nestedSizes;
		}

		public List<Integer> getSizes() {
			return sizes;
		}

		public void setSizes(List<Integer> sizes) {
			this.sizes = sizes;
		}
	}
	
	@Test
	public void testListInfo() {
		
		FieldCopyService copySvc = DefaultCopyFactory.Factory().createCopyService();
		List<FieldPair> pairL = copySvc.buildAutoCopyPairs(Taxi.class, TaxiDTO.class);
		
		Taxi taxi = new Taxi();
		for(FieldPair pair: pairL) {
			
			BeanUtilsFieldDescriptor fd = (BeanUtilsFieldDescriptor) pair.srcProp;
			ListSpec spec = ReflectionUtil.buildListSpec(taxi, fd);
			log(String.format("%s: %d - %s", pair.srcProp.getName(), spec.depth, spec.elementClass));
		}
	}
	
	
	@Test
	public void testInteger() {
		Taxi taxi = createTaxi();
		taxi.setNestedSizes(null);
		List<Integer> list = Arrays.asList(100,200,300);
		taxi.setSizes(list);
		TaxiDTO dto = new TaxiDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(taxi, dto).autoCopy().execute();
		assertEquals(55, dto.getWidth());
		assertEquals(3, dto.getSizes().size());
		chkIntListValue(3, 100, 200, dto.getSizes());
	}
	
	@Test
	public void testNestedInteger() {
		Taxi taxi = createTaxi();
		TaxiDTO dto = new TaxiDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(taxi, dto).autoCopy().execute();
		assertEquals(55, dto.getWidth());
		assertEquals(2, dto.getNestedSizes().size());
		chkIntListValue(3, 100, 200, dto.getNestedSizes().get(0));
		chkIntListValue(2, 44, 45, dto.getNestedSizes().get(1));
	}
	
	@Test
	public void testBean() {
		Taxi taxi = createTaxi();
		taxi.sources = new ArrayList<>();
		List<Source> list = taxi.sources;
		list.add(new Source("bob", 33));
		list.add(new Source("sue", 44));
		
		TaxiDTO dto = new TaxiDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(taxi, dto).autoCopy().execute();
		assertEquals(55, dto.getWidth());
		assertEquals(2, dto.getNestedSizes().size());
		
		assertEquals(2, dto.getSources().size());
		List<Dest> list2 = dto.getSources();
		assertEquals("bob", list2.get(0).getName());
		assertEquals("sue", list2.get(1).getName());
	}
	
	@Test
	public void testNestedBean() {
		Taxi taxi = createTaxi();
		taxi.nestedSources = new ArrayList<>();
		List<Source> list = new ArrayList<>();
		list.add(new Source("bob", 33));
		list.add(new Source("sue", 44));
		taxi.nestedSources.add(list);
		
		TaxiDTO dto = new TaxiDTO();
		
		FieldCopier copier = createCopier();
		copier.copy(taxi, dto).autoCopy().execute();
		assertEquals(55, dto.getWidth());
		assertEquals(2, dto.getNestedSizes().size());
		
		assertEquals(1, dto.getNestedSources().size());
		List<Dest> list2 = dto.getNestedSources().get(0);
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
