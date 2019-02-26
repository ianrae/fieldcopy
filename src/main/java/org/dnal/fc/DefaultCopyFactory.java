package org.dnal.fc;

import org.dnal.fc.beanutils.BeanUtilFieldCopyService;
import org.dnal.fc.core.CopyFactory;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;

public class DefaultCopyFactory implements CopyFactory {
	private static DefaultCopyFactory theSingleton;
	
	public static CopyFactory Factory() {
		if (theSingleton == null) {
			theSingleton = new DefaultCopyFactory();
		}
		return theSingleton;
	}

	@Override
	public SimpleLogger createLogger() {
		return new SimpleConsoleLogger();
	}

	@Override
	public FieldCopyService createCopyService() {
		SimpleLogger logger = createLogger();
		FieldRegistry registry = new FieldRegistry();
		FieldCopyService copySvc = new BeanUtilFieldCopyService(logger, registry);
		return copySvc;
	}

	@Override
	public FieldCopier createCopier() {
		FieldCopyService copySvc = createCopyService();
		FieldCopier builder = new FieldCopier(copySvc);
		return builder;
	}
}