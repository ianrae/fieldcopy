package org.dnal.fieldcopy;

import static org.junit.Assert.*;

import org.dnal.fieldcopy.ReqCustomRunner.ReqResult;
import org.dnal.fieldcopy.scopetest.BooleanTests;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ReqScopeTests {

	@Test
	public void test() {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		ReqCustomRunner.reqResult = null;
		
		Result result = junit.run(BooleanTests.class);		
		System.out.println(String.format("winni: %d", result.getFailureCount()));
		assertNotNull(ReqCustomRunner.reqResult);
		
		String s = ReqCustomRunner.reqResult.xx;
		System.out.println(String.format("winnir: %s", s));
		for(String ss: ReqCustomRunner.reqResult.tests) {
			log("  " + ss);
		}
		
	}
	
	private void log(String s) {
		System.out.println(s);
	}
}
