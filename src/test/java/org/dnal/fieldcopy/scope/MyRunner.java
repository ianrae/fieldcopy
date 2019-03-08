package org.dnal.fieldcopy.scope;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;

public class MyRunner extends BlockJUnit4ClassRunner {
	public static boolean enableScopeProcessing;
	public static ScopeTestRunResults scopeResults;

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
			scopeResults = new ScopeTestRunResults();
			listener.results = scopeResults;
			notifier.addListener(listener);
		}
		notifier.fireTestRunStarted(getDescription());
		super.run(notifier);
		
		if (enableScopeProcessing) {
			System.out.println("---");
			for(ScopeResult res: listener.results.executions) {
				System.out.println(String.format("[%b] %s", res.pass, res.scope));
			}
		}
	}
}