package org.dnal.fieldcopy;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class MyRunner extends BlockJUnit4ClassRunner {
	public static class MyTestListener extends RunListener {
		public ScopeTestRunResults results;
		
	    public void testRunStarted(Description description) throws Exception {
	        System.out.println("Number of tests to execute: " + description.testCount());
	        if (results != null) {
	        	results.scope = getClassScopeValue(description);
	        }
	    }
	    
	    private String getClassScopeValue(Description desc) {
        	Class<?> testClass = desc.getTestClass();
        	String testClassName = testClass.getSimpleName();
	        System.out.println("TCCC: " + testClassName);
	        if (testClass.isAnnotationPresent(Scope.class)) {
        		Scope[] ar = testClass.getAnnotationsByType(Scope.class);
        		if (ar.length > 0) {
        			Scope scope = ar[0];
        			return scope.value();
        		}
	        }
	    	return null;
	    }
	    private String getMethodScopeValue(Description desc) {
        	Class<?> testClass = desc.getTestClass();
        	Method meth = findTestMethod(testClass, desc.getMethodName());
        	if (meth != null) {
        		Scope[] ar = meth.getAnnotationsByType(Scope.class);
        		if (ar.length > 0) {
        			Scope scope = ar[0];
        			return scope.value();
        		}

        	}
	    	return null;
	    }
	    private String getMethodScopeTarget(Description desc) {
        	Class<?> testClass = desc.getTestClass();
        	Method meth = findTestMethod(testClass, desc.getMethodName());
        	if (meth != null) {
        		Scope[] ar = meth.getAnnotationsByType(Scope.class);
        		if (ar.length > 0) {
        			Scope scope = ar[0];
        			return scope.target();
        		}

        	}
	    	return null;
	    }
	    private Method findTestMethod(Class<?> clazz, String methodName) {
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(methodName)) {
                	return method;
                }
            }
            return null;
	    }
	    
	    public void testRunFinished(Result result) throws Exception {
	    }
	    public void testStarted(Description description) throws Exception {
	    }

	    public void testFinished(Description description) throws Exception {
	    	scopeExecution(description, "PASS");
	    }
	    
	    private void scopeExecution(Description desc, String prefix) {
	        if (results != null) {
	        	String target = getMethodScopeTarget(desc);
	        	String detail = getMethodScopeValue(desc);
	        	if (StringUtils.isNotEmpty(detail)) {
	        		target = StringUtils.isNotEmpty(target) ? target : "";
	        		String s = String.format("[%s] %s:%s: %s", prefix, results.scope, target, detail);
	        		results.executions.add(s);
	        	}
	        }
	    	
	    }

	    public void testFailure(Failure failure) throws Exception {
	    	scopeExecution(failure.getDescription(), "FAIL");
	    }

	    public void testAssumptionFailure(Failure failure) {
	    	scopeExecution(failure.getDescription(), "FAIL");
	    }

	    public void testIgnored(Description description) throws Exception {
	        //System.out.println("Ignored: " + description.getMethodName());
	    }
	}
	
    public MyRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }
 
//    @Override
//    protected Statement methodInvoker(FrameworkMethod method, Object test) {
//        //System.out.println("invoking: " + method.getName());
//        return super.methodInvoker(method, test);
//    }

	@Override
	public void run(RunNotifier notifier) {
		MyTestListener listener = new MyTestListener();
		listener.results = new ScopeTestRunResults();
		
		notifier.addListener(listener);
		notifier.fireTestRunStarted(getDescription());
		super.run(notifier);
		
		System.out.println("---");
		for(String s: listener.results.executions) {
			System.out.println(s);
		}
	}
}