package org.dnal.fieldcopy;


import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.planner.PlannerCopyFactory;

public class BaseTest {
	protected boolean usePlannerSvc = true;
	
	protected void log(String s) {
		System.out.println(s);
	}
	protected void enableLogging() {
		DefaultCopyFactory.Factory().createLogger().enableLogging(true);
	}

	protected FieldCopier createCopier() {
		if (usePlannerSvc) {
			PlannerCopyFactory.setLogger(new SimpleConsoleLogger());
			return PlannerCopyFactory.Factory().createCopier();
		} else {
			DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
			return DefaultCopyFactory.Factory().createCopier();
		}
	}
	//--
	protected FieldCopyService createCopyService() {
		if (usePlannerSvc) {
			return PlannerCopyFactory.Factory().createCopyService();
		} else {
			return DefaultCopyFactory.Factory().createCopyService();
		}
//		SimpleLogger logger = new SimpleConsoleLogger();
//		FieldRegistry registry = new FieldRegistry();
//		FieldCopyService copySvc = new FieldCopyService(logger, registry);
//		return copySvc;
	}
}
