package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;

import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.service.beanutils.BUBeanDetectorService;
import org.dnal.fieldcopy.service.beanutils.old.ArrayElementConverter;
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
		assertEquals(33, dest.getSizes()[0].intValue());
		assertEquals(35, dest.getSizes()[2].intValue());
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
	
	@Test
	public void testConverter() {
		String fieldName = "names";
		BUBeanDetectorService beanDetectorSvc = new BUBeanDetectorService();
		ArrayElementConverter conv = new ArrayElementConverter(Home.class, fieldName, String.class, String.class, beanDetectorSvc);
		
		FieldInfo sourceInfo = new FieldInfo();
		sourceInfo.beanClass = Home.class;
		sourceInfo.fieldClass = String.class;
		sourceInfo.fieldName = fieldName;
		
		FieldInfo destInfo = new FieldInfo();
		destInfo.beanClass = HomeDTO.class;
		destInfo.fieldClass = String.class;
		destInfo.fieldName = fieldName;
		
		assertEquals(true, conv.canConvert(sourceInfo, destInfo));
		
		Home src = new Home();
		String[] ar=  {"33", "34", "35"};
		src.setNames(ar);
		
		ConverterContext ctx = new ConverterContext();
		ctx.copyOptions = new CopyOptions();
		ctx.copySvc = this.createCopyService();
		ctx.destClass = HomeDTO.class;
		ctx.srcClass = Home.class;
		
		Object result = conv.convertValue(src, src.getNames(), ctx);
		
		assertEquals(true, result.getClass().isArray());
		//http://tutorials.jenkov.com/java-reflection/arrays.html
		Class<?> elClass = result.getClass().getComponentType();
		assertEquals(String.class, elClass);
		
		String s = (String) Array.get(result, 1);
		assertEquals("34", s);
	}
	
	@Test
	public void testConverter2() {
		String fieldName = "names";
		BUBeanDetectorService beanDetectorSvc = new BUBeanDetectorService();
		ArrayElementConverter conv = new ArrayElementConverter(Home.class, fieldName, String.class, Integer.class, beanDetectorSvc);
		
		FieldInfo sourceInfo = new FieldInfo();
		sourceInfo.beanClass = Home.class;
		sourceInfo.fieldClass = String.class;
		sourceInfo.fieldName = fieldName;
		
		FieldInfo destInfo = new FieldInfo();
		destInfo.beanClass = HomeDTO.class;
		destInfo.fieldClass = Integer.class;
		destInfo.fieldName = "sizes";
		
		assertEquals(true, conv.canConvert(sourceInfo, destInfo));
		
		Home src = new Home();
		String[] ar=  {"33", "34", "35"};
		src.setNames(ar);
		
		ConverterContext ctx = new ConverterContext();
		ctx.copyOptions = new CopyOptions();
		ctx.copySvc = this.createCopyService();
		ctx.destClass = HomeDTO.class;
		ctx.srcClass = Home.class;
		
		Object result = conv.convertValue(src, src.getNames(), ctx);
		
		assertEquals(true, result.getClass().isArray());
		//http://tutorials.jenkov.com/java-reflection/arrays.html
		Class<?> elClass = result.getClass().getComponentType();
		assertEquals(Integer.class, elClass);
		
		Integer n = (Integer) Array.get(result, 1);
		assertEquals(34, n.intValue());
	}
	
	//--
}
