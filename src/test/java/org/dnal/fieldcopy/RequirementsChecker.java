package org.dnal.fieldcopy;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Test;

public class RequirementsChecker {
	
	public static class ReqSpec {
		public String name;
		public List<ReqSpec> children = new ArrayList<>();
	}
	public static class TestRequirement {
		public String name;
		public String scope;
		public List<TestObservation> list = new ArrayList<>();
		public List<TestObservation> missingList = new ArrayList<>();
		
		public TestRequirement(String name, String scope) {
			this.name = name;
			this.scope = scope;
		}
	}
	
	public static class TestObservation {
		public String name;
		public String item;
		
		public TestObservation(String name, String item) {
			this.name = name;
			this.item = item;
		}
	}
	
	public static class ReqChecker {
		private List<ReqSpec> reqL = new ArrayList<>();
		private List<TestRequirement> testRequirementL = new ArrayList<>();
		private List<TestObservation> observationL = new ArrayList<>();
		
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

		public void observe(String testName, String item) {
			TestObservation obs = new TestObservation(testName, item);
			observationL.add(obs);
		}

		public int check() {
			for(TestRequirement treq: this.testRequirementL) {
				buildScopeList(treq);
			}
			
			//do check
			int missingCount = 0;
			for(TestRequirement treq: this.testRequirementL) {
				for(TestObservation obs: treq.list) {
					if (! findInObservations(obs)) {
						treq.missingList.add(obs);
						missingCount++;
					}
				}
			}
			return missingCount;
		}

		private boolean findInObservations(TestObservation target) {
			for(TestObservation obs: observationL) {
				if (obs.name.equals(target.name) && obs.item.equals(target.item)) {
					return true;
				}
			}
			return false;
		}

		private void buildScopeList(TestRequirement treq) {
			String forAllScope = parseScope(treq.scope);
			
			Optional<ReqSpec> optSpec = this.reqL.stream().filter(x -> x.name.equals(forAllScope)).findFirst();
			if (optSpec.isPresent()) {
				for(ReqSpec inner: optSpec.get().children) {
					TestObservation obs = new TestObservation(treq.name, inner.name);
					treq.list.add(obs);
				}
			}
		}

		private String parseScope(String scope) {
			int pos = scope.indexOf(':');
			int pos2 = scope.indexOf('}');
			String s = scope.substring(pos + 1, pos2);
			return s;
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
		
		checker.observe("values", "A");
		
		int missingCount = checker.check();
		assertEquals(2, missingCount);
	}
	
	@Test
	public void testOK() {
		ReqChecker checker = new ReqChecker();
		checker.addSpec("Shape", "A", "B", "C");
		checker.addTestRequirement("values", "{x:Shape}");
		assertEquals(1, checker.size());
		ReqSpec req = checker.getList().get(0);
		assertEquals(3, req.children.size());
		
		checker.observe("values", "A");
		checker.observe("values", "B");
		checker.observe("values", "C");
		
		int missingCount = checker.check();
		assertEquals(0, missingCount);
	}
	
	@Test
	public void test2() {
		ReqChecker checker = new ReqChecker();
		checker.addSpec("Shape", "A", "B", "C");
		checker.addTestRequirement("values", "{x:Shape}");
		checker.addTestRequirement("nulls", "{x:Shape}");
		assertEquals(1, checker.size());
		ReqSpec req = checker.getList().get(0);
		assertEquals(3, req.children.size());
		
		checker.observe("values", "A");
		checker.observe("values", "B");
		checker.observe("values", "C");
		checker.observe("nulls", "C");
		
		int missingCount = checker.check();
		assertEquals(2, missingCount);
	}

}
