package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.scope.core.MyRunner;
import org.dnal.fieldcopy.scope.core.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.dnal.fieldcopy.scopetest.data.BaseListConverter;
import org.dnal.fieldcopy.scopetest.data.Colour;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("Colour[]")
public class ArrayColourTests extends BaseListTest {
	
	public static class MyStringToColourListConverter extends BaseListConverter {
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.matches("listString1");
		}

		@Override
		protected Object copyElement(Object el) {
			String str = el.toString();
			return Colour.valueOf(str);
		}
	}
	
	@Test
	@Scope("values")
	public void test() {
		doCopy(mainField);
		chkColourArrayValue(2, Colour.RED, Colour.BLUE);
		
		reset();
		Colour[] ar = { Colour.RED, Colour.BLUE, Colour.GREEN };
		entity.setArrayColour1(ar);
		doCopy(mainField);
		chkColourArrayValue(3, Colour.RED, Colour.BLUE);

		reset();
		ar = new Colour[]{};
		entity.setArrayColour1(ar);
		doCopy(mainField);
		chkColourArrayValue(0, null, null);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setArrayColour1(null);
		doCopy(mainField);
		assertEquals(null, dto.getArrayColour1());
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
		copySrcFieldToFail(mainField, "listInt1");
	}
	@Test
	@Scope("List<Long>")
	public void testToListLong() {
		copySrcFieldToFail(mainField, "listLong1");
	}
	@Test
	@Scope("List<String>")
	public void testToListString() {
		copySrcFieldTo(mainField, "listString1");
		chkValue(2, "RED", "BLUE");
	}
	@Test
	@Scope("List<Date>")
	public void testToListDate() {
		copySrcFieldToFail(mainField, "listDate1");
	}
	@Test
	@Scope("List<Colour>")
	public void testToListColour() {
		copySrcFieldTo(mainField, "listColour1");
		chkColourListValue(2, Colour.RED, Colour.BLUE);
		
		reset();
		copySrcFieldToFail(mainField, "listProvince1");
	}
	
	//--array--
	@Test
	@Scope("Integer[]")
	public void testToArrayInt() {
		copySrcFieldToFail(mainField, "arrayInt1");
	}
	@Test
	@Scope("String[]")
	public void testToArrayString() {
		enableLogging();
//		copier.copy(entity, dto).withConverters(new MyIntegerToStringArrayConverter()).field("arrayInt1", "listString1").execute();
		copier.copy(entity, dto).field("arrayColour1", "arrayString1").execute();
		chkStringArrayValue(2, "RED", "BLUE");
	}
	@Test
	@Scope("Date[]")
	public void testToArrayDate() {
		copySrcFieldToFail(mainField, "arrayDate1");
	}
	@Test
	@Scope("Long[]")
	public void testToArrayLong() {
		copySrcFieldToFail(mainField, "arrayLong1");
	}
	@Test
	@Scope("Colour[]")
	public void testToArrayColour() {
		copySrcFieldTo(mainField, "arrayColour1");
		chkColourArrayValue(2, Colour.RED, Colour.BLUE);
	}
	
	//---
	private static final String mainField = "arrayColour1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		Colour[] ar = createColourArray();
		entity.setArrayColour1(ar);
		
		return entity;
	}
}
