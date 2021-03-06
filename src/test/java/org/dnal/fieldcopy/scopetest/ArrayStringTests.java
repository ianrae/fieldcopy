package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.scope.core.MyRunner;
import org.dnal.fieldcopy.scope.core.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("String[]")
public class ArrayStringTests extends BaseListTest {
	
	@Test
	@Scope("values")
	public void test() {
		doCopy(mainField);
		chkStringArrayValue(2, "abc", "def");
		
		reset();
		String[] ar = {"abc", "def", "hij"};
		entity.setArrayString1(ar);
		doCopy(mainField);
		chkStringArrayValue(3, "abc", "def");

		reset();
		ar = new String[]{};
		entity.setArrayString1(ar);
		doCopy(mainField);
		chkStringArrayValue(0, null, null);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setArrayString1(null);
		doCopy(mainField);
		assertEquals(null, dto.getArrayString1());
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
		String[] ar = {"44", "45"};
		entity.setArrayString1(ar);
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
//		copier.copy(entity, dto).withConverters(new MyIntegerToStringArrayConverter()).field("arrayInt1", "listString1").execute();
		copier.copy(entity, dto).field("arrayString1", "listString1").execute();
		chkValue(2, "abc", "def");
	}
	@Test
	@Scope("List<Date>")
	public void testToListDate() {
		copySrcFieldToFail(mainField, "listDate1");
	}
	@Test
	@Scope("List<Long>")
	public void testToListLong() {
		reset();
		String[] ar = {"44", "45"};
		entity.setArrayString1(ar);
		copySrcFieldTo(mainField, "listLong1", false);
		chkLongListValue(2, 44L, 45L);
	}
	@Test
	@Scope("List<Colour>")
	public void testToLisColour() {
		copySrcFieldToFail(mainField, "listColour1");
	}
	
	//--array--
	@Test
	@Scope("Integer[]")
	public void testToArrayInt() {
		reset();
		String[] ar = {"44", "45"};
		entity.setArrayString1(ar);
		copySrcFieldTo(mainField, "arrayInt1", false);
		chkIntArrayValue(2, 44, 45);
		
		reset();
		copySrcFieldTo(mainField, "arrayInt1");
		//TODO: fix bug. converts to 0. should really be a conversion error
		//since "abc" can't be converted to 0
		chkIntArrayValue(2, 0, 0);
	}
	@Test
	@Scope("String[]")
	public void testToArrayString() {
//		copier.copy(entity, dto).withConverters(new MyIntegerToStringArrayConverter()).field("arrayInt1", "listString1").execute();
		copier.copy(entity, dto).field("arrayString1", "arrayString1").execute();
		chkStringArrayValue(2, "abc", "def");
	}
	@Test
	@Scope("Date[]")
	public void testToArrayDate() {
		copySrcFieldToFail(mainField, "arrayDate1");
	}
	@Test
	@Scope("Long[]")
	public void testToArrayLong() {
		reset();
		String[] ar = {"44", "45"};
		entity.setArrayString1(ar);
		copySrcFieldTo(mainField, "arrayLong1", false);
		chkLongArrayValue(2, 44L, 45L);
	}
	@Test
	@Scope("Colour[]")
	public void testToArrayColour() {
		copySrcFieldToFail(mainField, "arrayColour1");
	}
	
	//---
	private static final String mainField = "arrayString1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		String[] ar = createStringArray();
		entity.setArrayString1(ar);
		
		return entity;
	}
}
