//package org.dnal.fieldcopy;
//
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.dnal.fieldcopy.scope.Scope;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.Description;
//import org.junit.runner.Runner;
//import org.junit.runner.notification.Failure;
//import org.junit.runner.notification.RunNotifier;
//import org.junit.runners.model.InitializationError;
//
////https://www.baeldung.com/junit-4-custom-runners
//public class ReqCustomRunner extends Runner  {
//	
//	public static class ReqResult {
//		public String xx;
//		public List<String> tests = new ArrayList<>();
//	}
//	public static ReqResult reqResult = null;
//	 
//    private Class<?> testClass;
//    public ReqCustomRunner(Class<?> testClass) throws InitializationError {
//        super();
//        this.testClass = testClass;
//    }
// 
//    @Override
//    public Description getDescription() {
//        return Description.createTestDescription(testClass, "My runner description");
//    }
// 
//    @Override
//    public void run(RunNotifier notifier) {
//        System.out.println("running the tests from MyRunner: " + testClass);
//    	Description descr = null; 
//    	reqResult = new ReqResult();
//    	reqResult.xx = "abc";
//    	
//    	
//        try {
//            Object testObject = testClass.newInstance();
//            for (Method method : testClass.getMethods()) {
//            	descr = Description.createTestDescription(testClass, method.getName());
//                if (method.isAnnotationPresent(Test.class)) {
//                	runInit(notifier);
//                	
//                	if (method.isAnnotationPresent(Scope.class)) {
//                		Scope[] ar = method.getAnnotationsByType(Scope.class);
//                		if (ar.length > 0) {
//                			Scope scope = ar[0];
//                			reqResult.tests.add(scope.value());
//                		}
//                	}
//                	
//                    notifier.fireTestStarted(descr);
//                    method.invoke(testObject);
//                    notifier.fireTestFinished(descr);
//                }
//            }
//        } catch (InvocationTargetException e) {
//        	Throwable thr = e.getCause();
//            //System.out.println("InvocationTargetException: " + thr.getMessage());
//            Failure ff = new Failure(descr, thr);
//            notifier.fireTestFailure(ff);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//    
//    private void runInit(RunNotifier notifier) {
//        try {
//            Object testObject = testClass.newInstance();
//            for (Method method : testClass.getMethods()) {
//                if (method.isAnnotationPresent(Before.class)) {
//                    method.invoke(testObject);
//                }
//            }
//        } catch (InvocationTargetException e) {
//        	Throwable thr = e.getCause();
//            System.out.println("INIT-InvocationTargetException: " + thr.getMessage());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//    
//}