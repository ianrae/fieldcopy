package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.scope.core.MyRunner;
import org.dnal.fieldcopy.scope.core.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.dnal.fieldcopy.scopetest.data.BaseListConverter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("List<Integer>")
public class ListIntegerTests extends BaseListTest {
	
	public static class MyIntegerToStringListConverter extends BaseListConverter {
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.matches("listInt1");
		}

		@Override
		protected Object copyElement(Object el) {
			Integer n = (Integer) el;
			return n.toString();
		}
	}
	
	@Test
	@Scope("values")
	public void test() {
		doCopy(mainField);
		chkIntListValue(2, 44, 45);
		
		reset();
		List<Integer> list = createIntList();
		list.add(66);
		entity.setListInt1(list);;
		doCopy(mainField);
		chkIntListValue(3, 44, 45);

		reset();
		list = createIntList();
		list.clear();
		entity.setListInt1(list);;
		doCopy(mainField);
		chkIntListValue(0, 0, 0);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setListInt1(null);
		doCopy(mainField);
		assertEquals(null, dto.getListInt1());
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
		chkIntListValue(2, 44, 45);
	}
	@Test
	@Scope("List<String>")
	public void testToListString() {
		copier.copy(entity, dto).withConverters(new MyIntegerToStringListConverter()).field("listInt1", "listString1").execute();
		chkValue(2, "44", "45");
	}
	@Test
	@Scope("List<Date>")
	public void testToListDate() {
		copySrcFieldToFail(mainField, "listDate1");
	}
	@Test
	@Scope("List<Long>")
	public void testToListLong() {
		copySrcFieldTo(mainField, "listLong1");
		chkLongListValue(2, 44L, 45L);
	}
	@Test
	@Scope("List<Colour>")
	public void testToLisColour() {
		copySrcFieldToFail(mainField, "listColour1");
	}
	
	//--array--
	@Test
	@Scope("String[]")
	public void testToArrayString() {
		reset();
		List<Integer> list = createIntList();
		entity.setListInt1(list);
		copier.copy(entity, dto).field("listInt1", "arrayString1").execute();
		chkStringArrayValue(2, "44", "45");
	}
	@Test
	@Scope("Integer[]")
	public void testToArrayInt() {
		copier.copy(entity, dto).field("listInt1", "arrayInt1").execute();
		chkIntArrayValue(2, 44, 45);
	}
	@Test
	@Scope("Date[]")
	public void testToArrayDate() {
		copySrcFieldToFail(mainField, "listDate1");
	}
	@Test
	@Scope("Long[]")
	public void testToArrayLong() {
		copier.copy(entity, dto).field("listInt1", "arrayLong1").execute();
		chkLongArrayValue(2, 44, 45);
	}
	@Test
	@Scope("Colour[]")
	public void testToArrayColour() {
		//not supported without a converter
		copySrcFieldToFail(mainField, "arrayColour1", false);
	}
	
	
	//---
	private static final String mainField = "listInt1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		List<Integer> list = createIntList();
		entity.setListInt1(list);
		
		return entity;
	}
}
