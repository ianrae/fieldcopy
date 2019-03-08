package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.InvocationTargetException;

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
}
