package org.dnal.fieldcopy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

//https://www.baeldung.com/junit-4-custom-runners
public class ReqCustomRunner extends BlockJUnit4ClassRunner  {
	 
    private Class<?> testClass;
    public ReqCustomRunner(Class<?> testClass) throws InitializationError {
        super(testClass);
        this.testClass = testClass;
    }
 
    @Override
    public Description getDescription() {
        return Description
          .createTestDescription(testClass, "My runner description");
    }
 
    @Override
    public void run(RunNotifier notifier) {
        System.out.println("running the tests from MyRunner: " + testClass);
    	Description descr = null; 
        try {
            Object testObject = testClass.newInstance();
            for (Method method : testClass.getMethods()) {
            	descr = Description.createTestDescription(testClass, method.getName());
                if (method.isAnnotationPresent(Test.class)) {
                    notifier.fireTestStarted(descr);
                    method.invoke(testObject);
                    notifier.fireTestFinished(descr);
                }
            }
        } catch (InvocationTargetException e) {
        	Throwable thr = e.getCause();
            //System.out.println("InvocationTargetException: " + thr.getMessage());
            Failure ff = new Failure(descr, thr);
            notifier.fireTestFailure(ff);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}