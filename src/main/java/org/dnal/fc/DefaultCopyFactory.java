package org.dnal.fc;

import org.dnal.fc.beanutils.BeanUtilFieldCopyService;
import org.dnal.fc.core.FieldFilter;
import org.dnal.fc.core.CopyFactory;
import org.dnal.fc.core.DefaultFieldFilter;
import org.dnal.fc.core.FieldCopyService;
import org.dnal.fc.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleConsoleLogger;
import org.dnal.fieldcopy.log.SimpleLogger;

public class DefaultCopyFactory implements CopyFactory {
	private static DefaultCopyFactory theSingleton;
	private static SimpleLogger theLogger;
	
	public static CopyFactory Factory() {
		if (theSingleton == null) {
			theSingleton = new DefaultCopyFactory();
		}
		return theSingleton;
	}

	@Override
	public SimpleLogger createLogger() {
		if (theLogger != null) {
			return theLogger;
		}
		return new SimpleConsoleLogger();
	}

	@Override
	public FieldCopyService createCopyService() {
		SimpleLogger logger = createLogger();
		FieldRegistry registry = new FieldRegistry();
		FieldFilter fieldFilter = createFieldFilter();
		FieldCopyService copySvc = new BeanUtilFieldCopyService(logger, registry, fieldFilter);
		return copySvc;
	}

	@Override
	public FieldCopier createCopier() {
		FieldCopyService copySvc = createCopyService();
		FieldCopier builder = new FieldCopier(copySvc);
		return builder;
	}
	
	public static void setLogger(SimpleLogger logger) {
		theLogger = logger;
	}

	@Override
	public FieldFilter createFieldFilter() {
		return new DefaultFieldFilter();
	}
}