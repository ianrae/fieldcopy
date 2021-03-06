package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Date;
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
@Scope("List<Long>")
public class ListLongTests extends BaseListTest {
	
	public static class MyLongToStringListConverter extends BaseListConverter {
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.matches("listLong1");
		}

		@Override
		protected Object copyElement(Object el) {
			Long n = (Long) el;
			return n.toString();
		}
	}
	
	@Test
	@Scope("values")
	public void test() {
		doCopy(mainField);
		chkLongListValue(2, 44L, 45L);
		
		reset();
		List<Long> list = createLongList();
		list.add(66L);
		entity.setListLong1(list);;
		doCopy(mainField);
		chkLongListValue(3, 44L, 45L);

		reset();
		list = createLongList();
		list.clear();
		entity.setListLong1(list);;
		doCopy(mainField);
		chkLongListValue(0, 0, 0);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setListLong1(null);
		doCopy(mainField);
		assertEquals(null, dto.getListLong1());
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
	@Scope("List<Long>")
	public void testToListLong() {
		copySrcFieldTo(mainField, "listLong1");
		chkLongListValue(2, 44L, 45L);
	}
	@Test
	@Scope("List<String>")
	public void testToListString() {
		copier.copy(entity, dto).withConverters(new MyLongToStringListConverter()).field("listLong1", "listString1").execute();
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
	@Scope("List<Colour>")
	public void testToListColour() {
		copySrcFieldToFail(mainField, "listColour1");
	}
	
	//--array--
	@Test
	@Scope("String[]")
	public void testToArrayString() {
		reset();
		List<Long> list = createLongList();
		entity.setListLong1(list);
		copier.copy(entity, dto).field("listLong1", "arrayString1").execute();
		chkStringArrayValue(2, "44", "45");
	}
	@Test
	@Scope("Integer[]")
	public void testToArrayInt() {
		copier.copy(entity, dto).field("listLong1", "arrayInt1").execute();
		chkIntArrayValue(2, 44, 45);
	}
	@Test
	@Scope("Date[]")
	public void testToArrayDate() {
		copier.copy(entity, dto).field("listLong1", "arrayDate1").execute();
		refDate1 = new Date(44L);
		refDate2 = new Date(45L);
		this.chkDateArrayValue(2, refDate1, refDate2);
	}
	@Test
	@Scope("Long[]")
	public void testToArrayLong() {
		copier.copy(entity, dto).field("listLong1", "arrayLong1").execute();
		chkLongArrayValue(2, 44, 45);
	}
	@Test
	@Scope("Colour[]")
	public void testToArrayColour() {
		//not supported without a converter
		copySrcFieldToFail(mainField, "arrayColour1", false);
	}
	
	//---
	private static final String mainField = "listLong1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		List<Long> list = createLongList();
		entity.setListLong1(list);
		
		return entity;
	}
}
