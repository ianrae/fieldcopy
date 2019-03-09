package org.dnal.fieldcopy.scope;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public abstract class MyScopeTestsBase {
	public List<String> allTypes;
	public List<String> errors = new ArrayList<>();
	protected ScopeTestRunResults scopeResults;
	
	public abstract boolean checkResults(ScopeTestRunResults scopeResults);

	protected void checkAll() {
		for(String type: allTypes) {
			for(String inner: allTypes) {
				if (inner.equals(type)) {
					continue;
				}
				String s = String.format("%s:: %s", type, inner);
				ScopeResult res = findTarget(s);
				addErrorIfFailed("checkAll", res, type, inner);
			}
		}
	}
	protected void addErrorIfFailed(String name, ScopeResult res, String s1, String s2) {
		String title = StringUtils.isEmpty(name) ? "" : String.format("(%s)", name);
		if (res == null) {
			errors.add(String.format("%s:: %s MISSING %s", s1, s2, title));
		} else if (!res.pass) {
			errors.add(String.format("%s:: %s FAILED %s", s1, s2, title));
		}
	}

	protected void checkPrimitive(String mainType, String primitiveType) {
		for(String type: allTypes) {
			if (mainType.startsWith(type)) {
				continue;
			}
			String target = String.format("%s:%s", mainType, primitiveType);
			
			String s = String.format("%s: %s", target, type);
			ScopeResult res = findTarget(s);
			addErrorIfFailed("checkPrimitives", res, target, type);
		}
	}

	protected void ensureHappened(String testName) {
		for(String type: allTypes) {
			ScopeResult res = find(type, testName);
			addErrorIfFailed("", res, type, testName);
		}
	}

	protected ScopeResult find(String type, String testName) {
		String s = String.format("%s:: %s", type, testName);
		return findTarget(s);
	}
	protected ScopeResult findTarget(String target) {
		for(ScopeResult res: scopeResults.executions) {
			if (res.scope.equals(target)) {
				return res;
			}
		}
		return null;
	}
	protected ScopeResult findTargetStartsWith(String target) {
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
	protected static void log(String s) {
		System.out.println(s);
	}

}
