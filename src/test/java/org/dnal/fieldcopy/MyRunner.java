package org.dnal.fieldcopy;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

public class MyRunner extends BlockJUnit4ClassRunner {
	public static class MyTestListener extends RunListener {
		public ScopeTestRunResults results;
		
	    public void testRunStarted(Description description) throws Exception {
	        System.out.println("Number of tests to execute: " + description.testCount());
	        if (results != null) {
	        	results.scope = getScopeValue(description);
	        }
	    }
	    
	    private String getScopeValue(Description desc) {
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
	    public void testRunFinished(Result result) throws Exception {
	       // System.out.println("Number of tests executed: " + result.getRunCount());
	    }

	    public void testStarted(Description description) throws Exception {
	       // System.out.println("Starting: " + description.getMethodName());
	    }

	    public void testFinished(Description description) throws Exception {
	       //.out.println("Finished: " + description.getMethodName());
	        if (results != null) {
	        	String detail = getScopeValue(description);
	        	if (detail != null) {
	        		String s = String.format("%s %s", results.scope, detail);
	        		results.executions.add(s);
	        	}
	        }
	    }

	    public void testFailure(Failure failure) throws Exception {
	       // System.out.println("Failed: " + failure.getDescription().getMethodName());
	    }

	    public void testAssumptionFailure(Failure failure) {
	        System.out.println("Failed: " + failure.getDescription().getMethodName());
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
	}
}