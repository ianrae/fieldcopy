package org.dnal.fieldcopy.scope;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.dnal.fieldcopy.BaseTest;
import org.dnal.fieldcopy.scope.core.MyRunner;
import org.dnal.fieldcopy.scope.core.MyScopeTestsBase;
import org.dnal.fieldcopy.scope.core.ScopeResult;
import org.dnal.fieldcopy.scope.core.ScopeTestRunResults;
import org.dnal.fieldcopy.scopetest.ArrayColourTests;
import org.dnal.fieldcopy.scopetest.ArrayDateTests;
import org.dnal.fieldcopy.scopetest.ArrayIntegerTests;
import org.dnal.fieldcopy.scopetest.ArrayLongTests;
import org.dnal.fieldcopy.scopetest.ArrayStringTests;
import org.dnal.fieldcopy.scopetest.BooleanTests;
import org.dnal.fieldcopy.scopetest.DateTests;
import org.dnal.fieldcopy.scopetest.DoubleTests;
import org.dnal.fieldcopy.scopetest.EnumTests;
import org.dnal.fieldcopy.scopetest.IntegerTests;
import org.dnal.fieldcopy.scopetest.ListDateTests;
import org.dnal.fieldcopy.scopetest.ListColourTests;
import org.dnal.fieldcopy.scopetest.ListIntegerTests;
import org.dnal.fieldcopy.scopetest.ListLongTests;
import org.dnal.fieldcopy.scopetest.ListStringTests;
import org.dnal.fieldcopy.scopetest.LongTests;
import org.dnal.fieldcopy.scopetest.StringTests;
import org.junit.Test;
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;

public class AllScopeTests extends BaseTest {
	
	public static class MyScopeTests extends MyScopeTestsBase {
		public MyScopeTests() {
			this.allTypes = Arrays.asList("Boolean", "Integer", "Long", "Double", 
					"String", "Date", "enum");
			
			this.allListTypes = Arrays.asList("List<String>", "List<Integer>", "List<Date>", 
					"List<Long>", "List<Colour>");
			
			this.allArrayTypes = Arrays.asList("String[]", "Integer[]", "Date[]", 
					"Long[]", "Colour[]");
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
			
			for(String arrayType: allArrayTypes) {
				ensureHappenedArray("values");
				ensureHappenedArray("null");
//				checkListType(listType);
//				checkListToListType(listType);
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
		
		//--array
		protected void ensureHappenedArray(String testName) {
			for(String type: allArrayTypes) {
				ScopeResult res = find(type, testName);
				addErrorIfFailed("", res, type, testName);
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
		runClass(ListLongTests.class);	
		runClass(ListColourTests.class);	
		
		runClass(ArrayColourTests.class);
		runClass(ArrayDateTests.class);
		runClass(ArrayIntegerTests.class);
		runClass(ArrayLongTests.class);
		runClass(ArrayStringTests.class);

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
}
