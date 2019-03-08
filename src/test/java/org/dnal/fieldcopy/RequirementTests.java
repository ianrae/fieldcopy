package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.Scope;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

//@RunWith(ReqCustomRunner.class)
@RunWith(MyRunner.class)
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
	@Test
	@Scope("abcdef")
	public void test3() {
		assertEquals(1,1);
	}
	@Test
	@Scope("abcdef")
	public void test4() {
		assertEquals(1,1);
	}
	
	@Before
	public void init()  {
		System.out.println("..ii..");
	}

}
