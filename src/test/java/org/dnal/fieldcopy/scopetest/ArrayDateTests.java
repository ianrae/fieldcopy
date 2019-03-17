package org.dnal.fieldcopy.scopetest;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.scope.core.MyRunner;
import org.dnal.fieldcopy.scope.core.Scope;
import org.dnal.fieldcopy.scopetest.data.AllTypesEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


@RunWith(MyRunner.class)
@Scope("Date[]")
public class ArrayDateTests extends BaseListTest {
	
	public static class MyDateToStringArrayConverter implements ValueConverter {
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.matches("arrayDate1");
		}

		@Override
		public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
			List<String> list = new ArrayList<>();
			int n = Array.getLength(value);
			for(int i = 0; i < n; i++) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date dt = (Date) Array.get(value, i);
				list.add(sdf.format(dt));
			}
			return list;
		}
	}
	
	@Test
	@Scope("values")
	public void test() {
		doCopy(mainField);
		chkDateArrayValue(2, refDate1, refDate2);
		
		reset();
		reset();
		Date[] ar = { createADate(0), createADate(1), createADate(2) };
		entity.setArrayDate1(ar);
		doCopy(mainField);
		chkDateArrayValue(3, refDate1, refDate2);

		reset();
		ar = new Date[]{ };
		entity.setArrayDate1(ar);
		doCopy(mainField);
		chkDateArrayValue(0, null, null);
	}
	
	@Test
	@Scope("null")
	public void testNull() {
		entity.setArrayDate1(null);
		doCopy(mainField);
		assertEquals(null, dto.getArrayDate1());
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
//		copySrcFieldTo(mainField, "listString1");
//		String s = "Fri Dec 25 07:30:41 EST 2015";
//		assertEquals(s, refDate1.toString());
////		copier.copy(entity, dto).withConverters(new MyIntegerToStringListConverter()).field("listInt1", "listString1").execute();
//		chkValue(2, formatDate(refDate1), formatDate(refDate2));
		
		reset();
		copier.copy(entity, dto).withConverters(new MyDateToStringArrayConverter()).field("arrayDate1", "listString1").execute();
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
	
	
	private String formatDate(Date dt) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String s  = sdf.format(dt);
		return s;
	}


	//---
	private static final String mainField = "arrayDate1";
	
	@Before
	public void init() {
		super.init();
	}
	@Override
	protected AllTypesEntity createEntity() {
		AllTypesEntity entity = new AllTypesEntity();
		
		Date[] ar = createDateArray();
		entity.setArrayDate1(ar);
		
		return entity;
	}
}
