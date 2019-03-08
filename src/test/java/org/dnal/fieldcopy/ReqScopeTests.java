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
			ensureHappened("null");
			checkPrimitive("Boolean:boolean");
//			checkPrimitive("Integer:int");
//			checkPrimitive("Double:double");
			checkAll();
			return errors.isEmpty();
		}

		private void checkAll() {
			for(String type: allTypes) {
				for(String inner: allTypes) {
					if (inner.equals(type)) {
						continue;
					}
					String s = String.format("%s:: %s", type, inner);
					ScopeResult res = findTarget(s);
					if (res == null) {
						errors.add(String.format("%s:: %s MISSING (checkAll)", type, inner));
					} else if (!res.pass) {
						errors.add(String.format("%s:: %s FAILED (checkAll)", type, inner));
					}
					
				}
			}
		}

		private void checkPrimitive(String target) {
			for(String type: allTypes) {
				if (target.startsWith(type)) {
					continue;
				}
				String s = String.format("%s: %s", target, type);
				ScopeResult res = findTarget(s);
				if (res == null) {
					errors.add(String.format("%s:: %s MISSING", target, type));
				} else if (!res.pass) {
					errors.add(String.format("%s:: %s FAILED", target, type));
				}
			}
		}

		private void ensureHappened(String testName) {
			for(String type: allTypes) {
				ScopeResult res = find(type, testName);
				if (res == null) {
					errors.add(String.format("%s:: %s - MISSING", type, testName));
				} else if (!res.pass) {
					errors.add(String.format("%s:: %s - FAILED", type, testName));
				}
			}
		}

		private ScopeResult find(String type, String testName) {
			String s = String.format("%s:: %s", type, testName);
			return findTarget(s);
		}
		private ScopeResult findTarget(String target) {
			for(ScopeResult res: scopeResults.executions) {
				if (res.scope.equals(target)) {
					return res;
				}
			}
			return null;
		}
		private ScopeResult findTargetStartsWith(String target) {
			for(ScopeResult res: scopeResults.executions) {
				if (res.scope.startsWith(target)) {
					return res;
				}
			}
			return null;
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
