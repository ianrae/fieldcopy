package org.dnal.fieldcopy;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.ReqCustomRunner.ReqResult;
import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.ScopeResult;
import org.dnal.fieldcopy.scope.ScopeTestRunResults;
import org.dnal.fieldcopy.scopetest.BooleanTests;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;

public class ReqScopeTests {
	
	public static class MyScopeTests {
		public List<String> allTypes = Arrays.asList("Boolean", "Integer");
		public List<String> errors = new ArrayList<>();
		private ScopeTestRunResults scopeResults;
		
		public boolean checkResults(ScopeTestRunResults scopeResults) {
			this.scopeResults = scopeResults;
			ensureHappened("values");
			return errors.isEmpty();
		}

		private void ensureHappened(String testName) {
			for(String type: allTypes) {
				if (! find(type, testName)) {
					errors.add(String.format("%s:: %s", type, testName));
				}
			}
		}

		private boolean find(String type, String testName) {
			for(ScopeResult res: scopeResults.executions) {
				String s = String.format("%s:: %s", type, testName);
				if (res.scope.equals(s)) {
					return true;
				}
			}
			return false;
		}

		public void dump() {
			log(String.format("--%d errors", errors.size()));
			for(String err: errors) {
				log(err);
			}
		}
		private static void log(String s) {
			System.out.println(s);
		}
		
	}

	@Test
	public void test() {
		JUnitCore junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		
		MyRunner.enableScopeProcessing = true;
		Result result = junit.run(BooleanTests.class);	
		
		MyScopeTests checker = new MyScopeTests();
		boolean b = checker.checkResults(MyRunner.scopeResults);
		checker.dump();
		assertEquals(true, b);
	}
	
	private static void log(String s) {
		System.out.println(s);
	}
}
