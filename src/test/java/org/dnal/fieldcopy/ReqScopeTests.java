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
		
		MyRunner.enableScopeProcessing = true;
		Result result = junit.run(BooleanTests.class);		
	}
	
	private void log(String s) {
		System.out.println(s);
	}
}
