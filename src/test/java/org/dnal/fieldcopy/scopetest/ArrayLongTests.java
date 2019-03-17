package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.dnal.fieldcopy.scope.core.MyRunner;
import org.dnal.fieldcopy.scope.core.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("Long[]")
public class ArrayLongTests extends BaseListTest {
	
	@Test
	@Scope("values")
	public void test() {
		doCopy(mainField);
		chkLongArrayValue(2, 44L, 45L);
		
		reset();
		Long[] ar = {44L, 45L, 46L};
		entity.setArrayLong1(ar);
		doCopy(mainField);
		chkLongArrayValue(3, 44, 45);

		reset();
		ar = new Long[]{};
		entity.setArrayLong1(ar);
		doCopy(mainField);
		chkLongArrayValue(0, 0, 0);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setArrayLong1(null);
		doCopy(mainField);
		assertEquals(null, dto.getArrayLong1());
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
//		copier.copy(entity, dto).withConverters(new MyIntegerToStringArrayConverter()).field("arrayInt1", "listString1").execute();
		copier.copy(entity, dto).field("arrayLong1", "listString1").execute();
		chkValue(2, "44", "45");
	}
	@Test
	@Scope("List<Date>")
	public void testToListDate() {
		copySrcFieldTo(mainField, "listDate1");
		refDate1 = new Date(44L);
		refDate2 = new Date(45L);
		this.chkDateListValue(2, refDate1, refDate2);
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
	@Scope("Integer[]")
	public void testToArrayInt() {
		copySrcFieldTo(mainField, "arrayInt1");
		chkIntArrayValue(2, 44, 45);
	}
	@Test
	@Scope("String[]")
	public void testToArrayString() {
		copier.copy(entity, dto).field("arrayLong1", "arrayString1").execute();
		chkArrayStringValue(2, "44", "45");
	}
	@Test
	@Scope("Date[]")
	public void testToArrayDate() {
		copySrcFieldTo(mainField, "arrayDate1");
		refDate1 = new Date(44L);
		refDate2 = new Date(45L);
		this.chkDateArrayValue(2, refDate1, refDate2);
	}
	@Test
	@Scope("Long[]")
	public void testToArrayLong() {
		copySrcFieldTo(mainField, "arrayLong1");
		chkLongArrayValue(2, 44L, 45L);
	}
	@Test
	@Scope("Colour[]")
	public void testToArrayColour() {
		copySrcFieldToFail(mainField, "arrayColour1");
	}
	
	//---
	private static final String mainField = "arrayLong1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		Long[] ar = createLongArray();
		entity.setArrayLong1(ar);
		
		return entity;
	}
}
