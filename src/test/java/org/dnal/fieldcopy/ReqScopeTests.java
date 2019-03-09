package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.dnal.fieldcopy.scope.MyRunner;
import org.dnal.fieldcopy.scope.MyScopeTestsBase;
import org.dnal.fieldcopy.scope.ScopeTestRunResults;
import org.dnal.fieldcopy.scopetest.BooleanTests;
import org.dnal.fieldcopy.scopetest.DateTests;
import org.dnal.fieldcopy.scopetest.DoubleTests;
import org.dnal.fieldcopy.scopetest.EnumTests;
import org.dnal.fieldcopy.scopetest.IntegerTests;
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
			return errors.isEmpty();
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
