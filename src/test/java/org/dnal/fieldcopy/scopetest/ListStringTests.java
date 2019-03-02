package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fc.core.ValueTransformer;
import org.junit.Before;
import org.junit.Test;


public class ListStringTests extends BaseScopeTest {
	
	public static class MyListTransformer implements ValueTransformer {
		@Override
		public boolean canHandle(String srcFieldName, Object value, Class<?> destClass) {
			if (srcFieldName.equals("listString1")) {
				return true;
			}
			return false;
		}

		@Override
		public Object transformValue(String srcFieldName, Object value, Class<?> destClass) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) value;
			
			List<Integer> list2 = new ArrayList<>();
			for(String s: list) {
				Integer n = Integer.parseInt(s);
				list2.add(n);
			}
			return list2;
		}
		
	}
	
	@Test
	public void test() {
		doCopy(mainField);
		chkValue(2, "abc", "def");
		
		reset();
		List<String> list = testList();
		list.add("x");
		entity.setListString1(list);;
		doCopy(mainField);
		chkValue(3, "abc", "def");

		reset();
		list = testList();
		list.clear();
		entity.setListString1(list);;
		doCopy(mainField);
		chkValue(0, null, null);
	}
	
	@Test
	public void testNull() {
		entity.setListString1(null);
		doCopy(mainField);
		assertEquals(null, dto.getListString1());
	}
	
	@Test
	public void testToBoolean() {
		copySrcFieldToFail(mainField, "primitiveBool");
	}
	@Test
	public void testToInt() {
		copySrcFieldToFail(mainField, "primitiveInt");
		copySrcFieldToFail(mainField, "int1");
	}
	@Test
	public void testToLong() {
		copySrcFieldToFail(mainField, "primitiveLong");
		copySrcFieldToFail(mainField, "long1");
	}
	@Test
	public void testToDouble() {
		copySrcFieldToFail(mainField, "primitiveDouble");
		copySrcFieldToFail(mainField, "double1");
	}
	@Test
	public void testToString() {
		copySrcFieldToFail(mainField, "string1");
	}
	@Test
	public void testToDate() {
		copySrcFieldToFail(mainField, "date1");
	}
	@Test
	public void testToEnum() {
		copySrcFieldToFail(mainField, "colour1");
	}
	
	@Test
	public void testToList() {
		copySrcFieldTo(mainField, "listInt1");
		//TODO: fix bug. the above line works but the list contains strings not integers!!
		//BeanUtils must simply be copying over the values
//		chkIntListValue(2, 0, 0);
		
		reset();
		List<String> list = Arrays.asList("44", "45");
		entity.setListString1(list);
		copier.copy(entity, dto).withTransformers(new MyListTransformer()).field("listString1", "listInt1").execute();
		chkIntListValue(2, 44, 45);
		
	}
	
	
	//---
	private static final String mainField = "listString1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		List<String> list = testList();
		entity.setListString1(list);
		
		return entity;
	}
	private List<String> testList() {
		List<String> list = Arrays.asList("abc", "def");
		list = new ArrayList<>(list);
		return list;
	}
	private List<Integer> testIntList() {
		List<Integer> list = Arrays.asList(44, 45);
		list = new ArrayList<>(list);
		return list;
	}
	
	protected void chkValue(int expected, String s1, String s2) {
		List<String> list = dto.getListString1();
		assertEquals(expected, list.size());
		
		if (expected > 0) {
			assertEquals(s1, list.get(0));
		}
		if (expected > 1) {
			assertEquals(s2, list.get(1));
		}
	}
	protected void chkIntListValue(int expected, int n1, int n2) {
		List<Integer> list = dto.getListInt1();
		assertEquals(expected, list.size());
		
		if (expected > 0) {
			assertEquals(n1, list.get(0).intValue());
		}
		if (expected > 1) {
			assertEquals(n2, list.get(1).intValue());
		}
	}
}
