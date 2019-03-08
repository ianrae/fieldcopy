package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.ScopeResult;
import org.dnal.fieldcopy.scope.ScopeTestRunResults;
import org.dnal.fieldcopy.scopetest.BooleanTests;
import org.dnal.fieldcopy.scopetest.IntegerTests;
import org.dnal.fieldcopy.scopetest.LongTests;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

public class ReqScopeTests {
	
	public static class MyScopeTests {
		public List<String> allTypes = Arrays.asList("Boolean", "Integer", "Long");
		public List<String> errors = new ArrayList<>();
		private ScopeTestRunResults scopeResults;
		
		public boolean checkResults(ScopeTestRunResults scopeResults) {
			this.scopeResults = scopeResults;
			ensureHappened("values");
			ensureHappened("null");
			checkPrimitive("Boolean", "boolean");
			checkPrimitive("Integer", "int");
			checkPrimitive("Long", "long");
//			checkPrimitive("Double", "double");
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
					addErrorIfFailed("checkAll", res, type, inner);
//					if (res == null) {
//						errors.add(String.format("%s:: %s MISSING (checkAll)", type, inner));
//					} else if (!res.pass) {
//						errors.add(String.format("%s:: %s FAILED (checkAll)", type, inner));
//					}
				}
			}
		}
		private void addErrorIfFailed(String name, ScopeResult res, String s1, String s2) {
			String title = StringUtils.isEmpty(name) ? "" : String.format("(%s)", name);
			if (res == null) {
				errors.add(String.format("%s:: %s MISSING %s", s1, s2, title));
			} else if (!res.pass) {
				errors.add(String.format("%s:: %s FAILED %s", s1, s2, title));
			}
		}

		private void checkPrimitive(String mainType, String primitiveType) {
			for(String type: allTypes) {
				if (mainType.startsWith(type)) {
					continue;
				}
				String target = String.format("%s:%s", mainType, primitiveType);
				
				String s = String.format("%s: %s", target, type);
				ScopeResult res = findTarget(s);
				addErrorIfFailed("checkPrimitives", res, target, type);
//				if (res == null) {
//					errors.add(String.format("%s:: %s MISSING", target, type));
//				} else if (!res.pass) {
//					errors.add(String.format("%s:: %s FAILED", target, type));
//				}
			}
		}

		private void ensureHappened(String testName) {
			for(String type: allTypes) {
				ScopeResult res = find(type, testName);
				addErrorIfFailed("", res, type, testName);
//				if (res == null) {
//					errors.add(String.format("%s:: %s - MISSING", type, testName));
//				} else if (!res.pass) {
//					errors.add(String.format("%s:: %s - FAILED", type, testName));
//				}
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
	
	//--
	private JUnitCore junit;
	private ScopeTestRunResults allResults;

	@Test
	public void test() {
		junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		
		allResults = new ScopeTestRunResults();
		MyRunner.enableScopeProcessing = true;
		
		//--all classes here--
		runClass(BooleanTests.class);
		runClass(IntegerTests.class);	
		runClass(LongTests.class);	
		
		MyScopeTests checker = new MyScopeTests();
		log(String.format("num-results: %d", allResults.executions.size()));
		boolean b = checker.checkResults(allResults);
		checker.dump();
		assertEquals(true, b);
	}
	
	private void runClass(Class<?> class1) {
		junit.run(class1);	
		allResults.executions.addAll(MyRunner.scopeResults.executions);
	}

	private static void log(String s) {
		System.out.println(s);
	}
}
