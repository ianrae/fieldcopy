package org.dnal.fieldcopy;


import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.service.beanutils.old.OldDefaultCopyFactory;

public class BaseTest {
	protected boolean usePlannerSvc = true;
	
	protected void log(String s) {
		System.out.println(s);
	}
	protected void enableLogging() {
		OldDefaultCopyFactory.Factory().createLogger().enableLogging(true);
	}

	protected FieldCopier createCopier() {
		if (usePlannerSvc) {
			DefaultCopyFactory.clearCopyService();
			DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
			return DefaultCopyFactory.Factory().createCopier();
		} else {
			OldDefaultCopyFactory.setLogger(new SimpleConsoleLogger());
			return OldDefaultCopyFactory.Factory().createCopier();
		}
	}
	//--
	protected FieldCopyService createCopyService() {
		if (usePlannerSvc) {
			return DefaultCopyFactory.Factory().createCopyService();
		} else {
			return OldDefaultCopyFactory.Factory().createCopyService();
		}
//		SimpleLogger logger = new SimpleConsoleLogger();
//		FieldRegistry registry = new FieldRegistry();
//		FieldCopyService copySvc = new FieldCopyService(logger, registry);
//		return copySvc;
	}
}
