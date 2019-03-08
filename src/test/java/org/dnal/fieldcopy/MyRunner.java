package org.dnal.fieldcopy;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class MyRunner extends BlockJUnit4ClassRunner {
	public static boolean enableScopeProcessing;
	
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
		if (enableScopeProcessing) {
			listener.results = new ScopeTestRunResults();
			notifier.addListener(listener);
		}
		notifier.fireTestRunStarted(getDescription());
		super.run(notifier);
		
		if (enableScopeProcessing) {
			System.out.println("---");
			for(String s: listener.results.executions) {
				System.out.println(s);
			}
		}
	}
}