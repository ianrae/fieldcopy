package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.dnal.fieldcopy.converter.ConverterContext;
import org.dnal.fieldcopy.converter.FieldInfo;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.junit.Test;

/**
 * 
 * @author Ian Rae
 *
 */
public class DateTests extends BaseTest {
	
	public static class MyDateConverter1 implements ValueConverter {
		private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		@Override
		public boolean canConvert(FieldInfo source, FieldInfo dest) {
			return source.fieldClass.equals(Date.class) && dest.fieldClass.equals(String.class);
		}
		@Override
		public Object convertValue(Object srcBean, Object value, ConverterContext ctx) {
			if (value == null) {
				return null;
			}
			Date dt = (Date) value;
			return sdf.format(dt);
		}
	}
	
	public static class Sample1{
		private Date dt;

		public Date getDt() {
			return dt;
		}

		public void setDt(Date dt) {
			this.dt = dt;
		}
	}	
	public static class Sample2 {
		private String dateStr;

		public String getDateStr() {
			return dateStr;
		}

		public void setDateStr(String dateStr) {
			this.dateStr = dateStr;
		}
	}
	
	@Test
	public void test() {
		Sample1 src = new Sample1();
		src.dt = createDate(2020, 03, 31);
		Sample2 dest = new Sample2();
		
		MyDateConverter1 conv = new MyDateConverter1();
		
		FieldCopier copier = createCopier();
		copier.addBuiltInConverter(conv);
		enableLogging();
		copier.copy(src, dest).field("dt", "dateStr").execute();
		assertEquals("2020-03-31", dest.getDateStr());

		log("again..");
		copier.copy(src, dest).autoCopy().execute();
		
	}
	
	protected Date createDate(int year, int mon, int day) {
	    Calendar cal = Calendar.getInstance();
	    cal.set(Calendar.YEAR, year);
	    cal.set(Calendar.MONTH, mon - 1);
	    cal.set(Calendar.DATE, day);
	    cal.set(Calendar.HOUR_OF_DAY, 7);
	    cal.set(Calendar.MINUTE, 30);
	    cal.set(Calendar.SECOND, 41);
	    cal.set(Calendar.MILLISECOND, 0);
	    Date dt = cal.getTime();
	    return dt;
	}
	
}
