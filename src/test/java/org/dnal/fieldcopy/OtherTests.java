package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class OtherTests {

	@Test
	public void test() {
		assertEquals(1,1);
		
		assertEquals("a", StringUtils.left("a", 3));
		
		String s = null;
		String ss = ObjectUtils.defaultIfNull(s, "?");
		assertEquals("?", ss);
	}
	
	@Test
	public void test2() throws IllegalAccessException, InvocationTargetException {
		Integer n1 = 44;
		Integer n2 = (Integer) ConvertUtils.convert(n1, Integer.class);
		assertEquals(44, n2.intValue());
	}
	
//	@Test
//	public void test3() {
//		SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss zzz yyyy");
//		Date dt = new Date();
//		String s1 = sdf.format(dt);
//		String s2 = dt.toString();
//		assertEquals(s1, s2);
//		
//		s1 = "Fri Dec 25 07:30:41 EST 2015";
//		boolean ok = false;
//		sdf.setLenient(false);
//		final ParsePosition pos = new ParsePosition(0);
//		final Date parsedDate = sdf.parse(s1, pos); // ignore the result (use the Calendar)
//		if (pos.getErrorIndex() >= 0 || pos.getIndex() != s1.length() || parsedDate == null) {
//			ok = false;
//		}
//		//dt = sdf.parse("Fri Dec 25 07:30:41 EST 2016");
//		assertEquals(true, ok);
//	}
}
