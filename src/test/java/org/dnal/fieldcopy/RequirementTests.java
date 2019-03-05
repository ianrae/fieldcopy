package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(ReqCustomRunner.class)
public class RequirementTests {

	@Test
	@Scope("abcd")
	public void test() {
		assertEquals(1,1);
	}
	@Test
	@Scope("abcdef")
	public void test2() {
		assertEquals(2,1);
	}

}
