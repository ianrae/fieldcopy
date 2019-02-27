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
		public boolean pass;
		
		public TestObservation(String name, String item, boolean pass) {
			this.name = name;
			this.item = item;
			this.pass = pass;
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

		public void observe(String testName, String item, boolean pass) {
			TestObservation obs = new TestObservation(testName, item, pass);
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
						System.out.println(String.format("miss or fail: %s: %s", obs.name, obs.item));
					}
				}
			}
			return missingCount;
		}

		private boolean findInObservations(TestObservation target) {
			for(TestObservation obs: observationL) {
				if (obs.name.equals(target.name) && obs.item.equals(target.item)) {
					return obs.pass;
				}
			}
			return false;
		}

		private void buildScopeList(TestRequirement treq) {
			String forAllScope = parseScope(treq.scope);
			if (forAllScope != null) {
				Optional<ReqSpec> optSpec = this.reqL.stream().filter(x -> x.name.equals(forAllScope)).findFirst();
				if (optSpec.isPresent()) {
					for(ReqSpec inner: optSpec.get().children) {
						TestObservation obs = new TestObservation(treq.name, inner.name, true);
						treq.list.add(obs);
					}
				}
			} else {
				String cpScope = parseCartesianProductScope(treq.scope);
				if (cpScope != null) {
					String itemA = cpScope.substring(1, cpScope.indexOf(':'));
					String itemB = cpScope.substring(cpScope.indexOf(':') + 1, cpScope.length() - 1);
					Optional<ReqSpec> optSpecA = this.reqL.stream().filter(x -> x.name.equals(itemA)).findFirst();
					Optional<ReqSpec> optSpecB = this.reqL.stream().filter(x -> x.name.equals(itemB)).findFirst();
					System.out.println("sdf");
					for(ReqSpec innerA: optSpecA.get().children) {
						for(ReqSpec innerB: optSpecB.get().children) {
							String ss = String.format("%s:%s", innerA.name, innerB.name);
							TestObservation obs = new TestObservation(treq.name, ss, true);
							treq.list.add(obs);
						}
					}
				}
			}
		}

		private String parseScope(String scope) {
			if (scope.contains(" X ")) {
				return null;
			}
			
			int pos = scope.indexOf(':');
			int pos2 = scope.indexOf('}');
			String s = scope.substring(pos + 1, pos2);
			return s;
		}
		
		private String parseCartesianProductScope(String scope) {
			if (scope.contains(" X ")) {
				String[] ar = scope.split(" ");
				String s = String.format("%s:%s", ar[0], ar[2]);
				return s;
			}
			return null;
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
		
		checker.observe("values", "A", true);
		
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
		
		checker.observe("values", "A", true);
		checker.observe("values", "B", true);
		checker.observe("values", "C", true);
		
		int missingCount = checker.check();
		assertEquals(0, missingCount);
	}
	
	@Test
	public void testFailingTest() {
		ReqChecker checker = new ReqChecker();
		checker.addSpec("Shape", "A", "B", "C");
		checker.addTestRequirement("values", "{x:Shape}");
		assertEquals(1, checker.size());
		ReqSpec req = checker.getList().get(0);
		assertEquals(3, req.children.size());
		
		checker.observe("values", "A", true);
		checker.observe("values", "B", false);
		checker.observe("values", "C", true);
		
		int missingCount = checker.check();
		assertEquals(1, missingCount);
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
		
		checker.observe("values", "A", true);
		checker.observe("values", "B", true);
		checker.observe("values", "C", true);
		checker.observe("nulls", "C", true);
		
		int missingCount = checker.check();
		assertEquals(2, missingCount);
	}
	
	@Test
	public void testCartesianProduct() {
		ReqChecker checker = new ReqChecker();
		checker.addSpec("Shape", "A", "B");
		checker.addTestRequirement("values", "{Shape X Shape}");
		assertEquals(1, checker.size());
		ReqSpec req = checker.getList().get(0);
		assertEquals(2, req.children.size());
		
		checker.observe("values", "A:A", true);
		checker.observe("values", "A:B", true);
		checker.observe("values", "B:A", true);
		checker.observe("values", "B:B", true);
		
		int missingCount = checker.check();
		assertEquals(0, missingCount);
	}
	

}
