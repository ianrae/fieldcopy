package org.dnal.fieldcopy.beanutils;

import static org.junit.Assert.assertEquals;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.IntegerConverter;
import org.junit.Test;

public class ApacheBeanUtilsTests {

	@Test
	public void test() {
		IntegerConverter conv = new IntegerConverter();
		String s = "140";
		Integer n = conv.convert(Integer.class, s);
		
		assertEquals(140, n.intValue());
	}
	@Test
	public void test2() {
		String s = "140";
		Integer n = (Integer) ConvertUtils.convert(s, Integer.class);
		
		assertEquals(140, n.intValue());
	}
}
