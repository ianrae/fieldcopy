package org.dnal.fieldcopy;

import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ReqTests {

	@Test
	public void test() {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		Result result = junit.run(RequirementTests.class);		
		System.out.println(String.format("winni: %d", result.getFailureCount()));
		
		String s = ReqCustomRunner.reqResult.xx;
		System.out.println(String.format("winnir: %s", s));
		
	}
}
