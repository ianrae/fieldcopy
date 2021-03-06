package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
@Scope("List<Colour>")
public class ListColourTests extends BaseListTest {
	
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
		chkColourListValue(2, Colour.RED, Colour.BLUE);
		
		reset();
		List<Colour> list = createColourList();
		list.add(Colour.GREEN);
		entity.setListColour1(list);;
		doCopy(mainField);
		chkColourListValue(3, Colour.RED, Colour.BLUE);

		reset();
		list = createColourList();
		list.clear();
		entity.setListColour1(list);;
		doCopy(mainField);
		chkColourListValue(0, null, null);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setListColour1(null);
		doCopy(mainField);
		assertEquals(null, dto.getListColour1());
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
	@Scope("String[]")
	public void testToArrayString() {
//		enableLogging();
		copier.copy(entity, dto).field("listColour1", "arrayString1").execute();
		chkStringArrayValue(2, "RED", "BLUE");
	}
	@Test
	@Scope("Integer[]")
	public void testToArrayInt() {
		copySrcFieldToFail(mainField, "arrayInt1");
	}
	@Test
	@Scope("Date[]")
	public void testToArrayDate() {
		copySrcFieldToFail(mainField, "listDate1");
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
		
		reset();
		copySrcFieldToFail(mainField, "arrayProvince1");
	}
	
	
	//---
	private static final String mainField = "listColour1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		List<Colour> list = createColourList();
		entity.setListColour1(list);
		
		return entity;
	}
}
