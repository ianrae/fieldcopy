package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
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
@Scope("List<Date>")
public class ListDateTests extends BaseListTest {
	
	public static class MyDateToStringListConverter extends BaseListConverter {
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.matches("listDate1");
		}

		@Override
		protected Object copyElement(Object el) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = (Date) el;
			return sdf.format(dt);
		}
	}
	
	@Test
	@Scope("values")
	public void test() {
		doCopy(mainField);
		chkDateListValue(2, refDate1, refDate2);
		
		reset();
		List<Date> list = createDateList();
		list.add(createADate(2));
		entity.setListDate1(list);
		doCopy(mainField);
		chkDateListValue(3, refDate1, refDate2);

		reset();
		list = createDateList();
		list.clear();
		entity.setListDate1(list);
		doCopy(mainField);
		chkDateListValue(0, null, null);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setListDate1(null);
		doCopy(mainField);
		assertEquals(null, dto.getListDate1());
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
		copySrcFieldTo(mainField, "listLong1");
		chkLongListValue(2, refDate1.getTime(), refDate2.getTime());
	}
	@Test
	@Scope("List<String>")
	public void testToListString() {
		copySrcFieldTo(mainField, "listString1");
		String s = "Fri Dec 25 07:30:41 EST 2015";
		assertEquals(s, refDate1.toString());
//		copier.copy(entity, dto).withConverters(new MyIntegerToStringListConverter()).field("listInt1", "listString1").execute();
		chkValue(2, formatDate(refDate1), formatDate(refDate2));
		
		reset();
		copier.copy(entity, dto).withConverters(new MyDateToStringListConverter()).field("listDate1", "listString1").execute();
		chkValue(2, "2015-12-25", "2016-12-25");
	}
	@Test
	@Scope("List<Date>")
	public void testToListDate() {
		copySrcFieldTo(mainField, "listDate1");
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
//		enableLogging();
		copier.copy(entity, dto).field("listDate1", "arrayString1").execute();
		chkStringArrayValue(2, formatDate(refDate1), formatDate(refDate2));
	}
	@Test
	@Scope("Integer[]")
	public void testToArrayInt() {
		copySrcFieldToFail(mainField, "arrayInt1");
	}
	@Test
	@Scope("Date[]")
	public void testToArrayDate() {
		copySrcFieldTo(mainField, "arrayDate1");
		this.chkDateArrayValue(2, refDate1, refDate2);
	}
	
	
	//---
	private static final String mainField = "listDate1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		List<Date> list = createDateList();
		entity.setListDate1(list);
		
		return entity;
	}
	private String formatDate(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s  = sdf.format(dt);
		return s;
	}
}
