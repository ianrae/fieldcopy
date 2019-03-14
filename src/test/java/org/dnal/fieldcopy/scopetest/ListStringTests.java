package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.dnal.fieldcopy.scopetest.data.BaseListConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("List<String>")
public class ListStringTests extends BaseListTest {
	
	public static class MyStringToIntegerListConverter extends BaseListConverter {
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
		List<String> list = createStringList();
		list.add("x");
		entity.setListString1(list);;
		doCopy(mainField);
		chkValue(3, "abc", "def");

		reset();
		list = createStringList();
		list.clear();
		entity.setListString1(list);
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
		reset();
		List<String> list = Arrays.asList("44", "45");
		entity.setListString1(list);
		copySrcFieldTo(mainField, "listInt1", false);
		chkIntListValue(2, 44, 45);
		
		reset();
		copySrcFieldTo(mainField, "listInt1");
		//TODO: fix bug. converts to 0. should really be a conversion error
		//since "abc" can't be converted to 0
		chkIntListValue(2, 0, 0);
	}
	@Test
	@Scope("List<String>")
	public void testToListString() {
		reset();
		List<String> list = Arrays.asList("44", "45");
		entity.setListString1(list);
		copier.copy(entity, dto).withConverters(new MyStringToIntegerListConverter()).field("listString1", "listInt1").execute();
		chkIntListValue(2, 44, 45);
	}
	@Test
	@Scope("List<Date>")
	public void testToListDate() {
//		reset();
//		List<String> list = Arrays.asList("44", "45");
//		entity.setListString1(list);
//		copySrcFieldToFail(mainField, "listDate1", false);
		
		reset();
		List<String> list = Arrays.asList("Fri Dec 25 07:30:41 EST 2015", "Fri Dec 25 07:30:41 EST 2016");
//		List<String> list = Arrays.asList("2015-12-25", "2016-12-25");
		entity.setListString1(list);
		copySrcFieldTo(mainField, "listDate1", false);
//		copier.copy(entity, dto).withConverters(new MyStringToIntegerListConverter()).field("listString1", "listDate1").execute();
		this.chkDateListValue(2, refDate1, refDate1);
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
		
		List<String> list = createStringList();
		entity.setListString1(list);
		
		return entity;
	}
}
