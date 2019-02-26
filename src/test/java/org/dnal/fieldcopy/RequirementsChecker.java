package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class RequirementsChecker {
	
	public static class ReqSpec {
		public String name;
		public List<ReqSpec> children = new ArrayList<>();
	}
	public static class TestRequirement {
		public String name;
		public String scope;
		
		public TestRequirement(String name, String scope) {
			this.name = name;
			this.scope = scope;
		}
	}
	
	public static class ReqChecker {
		private List<ReqSpec> reqL = new ArrayList<>();
		private List<TestRequirement> testRequirementL = new ArrayList<>();
		
		public void addSpec(String name, String...items) {
			ReqSpec req = new ReqSpec();
			req.name = name;
			for(String item: items) {
				ReqSpec child = new ReqSpec();
				child.name = item;
				req.children.add(child);
			}
			reqL.add(req);
		}
		
		public int size() {
			return reqL.size();
		}
		public List<ReqSpec> getList() {
			return reqL;
		}

		public void addTestRequirement(String testName, String scope) {
			TestRequirement treq = new TestRequirement(testName, scope);
			testRequirementL.add(treq);
		}
	}

	@Test
	public void test() {
		ReqChecker checker = new ReqChecker();
		checker.addSpec("Shape", "A", "B", "C");
		checker.addTestRequirement("values", "{x:Shape}");
		assertEquals(1, checker.size());
		ReqSpec req = checker.getList().get(0);
		assertEquals(3, req.children.size());
	}

}
