package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("List<String>")
public class ListStringTests extends BaseScopeTest {
	
	public static abstract class BaseListConverter implements ValueConverter {
		@Override
		public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
			@SuppressWarnings("unchecked")
			List<?> list = (List<?>) value;
			
			List<Object> list2 = new ArrayList<>();
			for(Object el: list) {
				Object copy = copyElement(el);
				list2.add(copy);
			}
			return list2;
		}

		protected abstract Object copyElement(Object el);
	}
	public static class MyListConverter extends BaseListConverter {
		@Override
		public boolean canHandle(String srcFieldName, Class<?>srcClass, Class<?> destClass) {
			return srcFieldName.equals("listString1");
		}

		@Override
		protected Object copyElement(Object el) {
			Integer n = Integer.parseInt(el.toString());
			return n;
		}

		@Override
		public void setCopySvc(FieldCopyService copySvc) {
		}
	}
	
	@Test
	@Scope("values")
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
	@Scope("null")
	public void testNull() {
		entity.setListString1(null);
		doCopy(mainField);
		assertEquals(null, dto.getListString1());
	}
	
	@Test
	@Scope("Boolean")
	public void testToBoolean() {
		copySrcFieldToFail(mainField, "primitiveBool");
		copySrcFieldToFail(mainField, "bool1");
	}
	@Test
	@Scope("Integer")
	public void testToInt() {
		copySrcFieldToFail(mainField, "primitiveInt");
		copySrcFieldToFail(mainField, "int1");
	}
	@Test
	@Scope("Long")
	public void testToLong() {
		copySrcFieldToFail(mainField, "primitiveLong");
		copySrcFieldToFail(mainField, "long1");
	}
	@Test
	@Scope("Double")
	public void testToDouble() {
		copySrcFieldToFail(mainField, "primitiveDouble");
		copySrcFieldToFail(mainField, "double1");
	}
	@Test
	@Scope("String")
	public void testToString() {
		copySrcFieldToFail(mainField, "string1");
	}
	@Test
	@Scope("Date")
	public void testToDate() {
		copySrcFieldToFail(mainField, "date1");
	}
	@Test
	@Scope("enum")
	public void testToEnum() {
		copySrcFieldToFail(mainField, "colour1");
	}
	
	@Test
	@Scope("List<Integer>")
	public void testToListInt() {
		copySrcFieldTo(mainField, "listInt1");
		//TODO: fix bug. the above line works but the list contains strings not integers!!
		//BeanUtils must simply be copying over the values
//		chkIntListValue(2, 0, 0);
	}
	@Test
	@Scope("List<String>")
	public void testToListString() {
		reset();
		List<String> list = Arrays.asList("44", "45");
		entity.setListString1(list);
		copier.copy(entity, dto).withConverters(new MyListConverter()).field("listString1", "listInt1").execute();
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
