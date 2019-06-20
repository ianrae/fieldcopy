package org.dnal.fieldcopy;


import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.metrics.SimpleCopyMetrics;

public class BaseTest {
	protected boolean usePlannerSvc = true;
	protected boolean enableMetrics = true;
	
	protected void log(String s) {
		System.out.println(s);
	}
	protected void enableLogging() {
		FieldCopy.getLogger().enableLogging(true);
	}

	protected FieldCopier createCopier() {
		if (usePlannerSvc) {
			CopierFactory fact1 = FieldCopy.createFactory();
			FieldCopier copier = fact1.createCopier();

			if (enableMetrics) {
				FieldCopyService svc = copier.getCopyService();
				SimpleCopyMetrics metrics = new SimpleCopyMetrics();
				svc.setMetrics(metrics);
			}
			
			return copier;
		} else {
//			OldDefaultCopyFactory.setLogger(new SimpleConsoleLogger());
//			return OldDefaultCopyFactory.Factory().createCopier();
			return null;
		}
	}
	//--
	protected FieldCopyService createCopyService() {
		if (usePlannerSvc) {
			return this.createCopier().getCopyService();
		} else {
			return null; //OldDefaultCopyFactory.Factory().createCopyService();
		}
	}
	
	protected void dumpMetrics(FieldCopier copier) {
		FieldCopyService svc = copier.getCopyService();
		SimpleCopyMetrics metrics = (SimpleCopyMetrics) svc.getMetrics();
		String s = String.format("%d plans (%d lazy): exec %d (%d fields)", metrics.planCount,
				metrics.lazyPlanCount, metrics.execCount, metrics.fieldExecCount);
		log(s);
	}
	
	
}
