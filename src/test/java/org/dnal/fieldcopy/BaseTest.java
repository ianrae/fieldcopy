package org.dnal.fieldcopy;


import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;

public class BaseTest {
	
	protected void log(String s) {
		System.out.println(s);
	}

	protected FieldCopier createCopier() {
		DefaultCopyFactory.setLogger(new SimpleConsoleLogger());
		return DefaultCopyFactory.Factory().createCopier();
	}
	//--
	protected FieldCopyService createCopyService() {
		return DefaultCopyFactory.Factory().createCopyService();
//		SimpleLogger logger = new SimpleConsoleLogger();
//		FieldRegistry registry = new FieldRegistry();
//		FieldCopyService copySvc = new FieldCopyService(logger, registry);
//		return copySvc;
	}
}
