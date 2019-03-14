package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.MyScopeTestsBase;
import org.dnal.fieldcopy.scope.ScopeResult;
import org.dnal.fieldcopy.scope.ScopeTestRunResults;
import org.dnal.fieldcopy.scopetest.BooleanTests;
import org.dnal.fieldcopy.scopetest.DateTests;
import org.dnal.fieldcopy.scopetest.DoubleTests;
import org.dnal.fieldcopy.scopetest.EnumTests;
import org.dnal.fieldcopy.scopetest.IntegerTests;
import org.dnal.fieldcopy.scopetest.ListDateTests;
import org.dnal.fieldcopy.scopetest.ListIntegerTests;
import org.dnal.fieldcopy.scopetest.ListStringTests;
import org.dnal.fieldcopy.scopetest.LongTests;
import org.dnal.fieldcopy.scopetest.StringTests;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

public class ReqScopeTests {
	
	public static class MyScopeTests extends MyScopeTestsBase {
		public MyScopeTests() {
			this.allTypes = Arrays.asList("Boolean", "Integer", "Long", "Double", 
					"String", "Date", "enum");
			
			this.allListTypes = Arrays.asList("List<String>", "List<Integer>", "List<Date>");
		}
		
		@Override
		public boolean checkResults(ScopeTestRunResults scopeResults) {
			this.scopeResults = scopeResults;
			ensureHappened("values");
			ensureHappened("null");
			checkPrimitive("Boolean", "boolean");
			checkPrimitive("Integer", "int");
			checkPrimitive("Long", "long");
			checkPrimitive("Double", "double");
			checkAll();
			
			for(String listType: allListTypes) {
				ensureHappenedList("values");
				ensureHappenedList("null");
				checkListType(listType);
				checkListToListType(listType);
			}
			
			//checkListAll();
			checkObserved();
			return errors.isEmpty();
		}
		
		protected void ensureHappenedList(String testName) {
			for(String type: allListTypes) {
				ScopeResult res = find(type, testName);
				addErrorIfFailed("", res, type, testName);
			}
		}
		protected void checkListType(String listType) {
			for(String type: allTypes) {
				String target = String.format("%s:: %s", listType, type);
				
				ScopeResult res = findTarget(target);
				addErrorIfFailed("checkListType", res, target, type);
			}
		}
		protected void checkListToListType(String listType) {
			for(String type: allListTypes) {
				String target = String.format("%s:: %s", listType, type);
				
				ScopeResult res = findTarget(target);
				addErrorIfFailed("checkListToListType", res, target, type);
			}
		}
	}
	
	//--
	private JUnitCore junit;
	private ScopeTestRunResults allResults;

	@Test
	public void test() {
		prepareForRunning();
		
		//--all classes here--
		runClass(BooleanTests.class);
		runClass(DateTests.class);	
		runClass(DoubleTests.class);	
		runClass(EnumTests.class);	
		runClass(IntegerTests.class);	
		runClass(LongTests.class);	
		runClass(StringTests.class);	
		
		runClass(ListStringTests.class);	
		runClass(ListIntegerTests.class);	
		runClass(ListDateTests.class);	

		afterRunning();
	}
	
	private void afterRunning() {
		MyScopeTests checker = new MyScopeTests();
		log(String.format("num-results: %d", allResults.executions.size()));
		boolean b = checker.checkResults(allResults);
		checker.dump();
		assertEquals(true, b);
	}

	private void prepareForRunning() {
		junit = new JUnitCore();
		junit.addListener(new TextListener(System.out));
		
		allResults = new ScopeTestRunResults();
		MyRunner.enableScopeProcessing = true;
	}

	private void runClass(Class<?> class1) {
		junit.run(class1);	
		allResults.executions.addAll(MyRunner.scopeResults.executions);
	}

	private static void log(String s) {
		System.out.println(s);
	}
}
