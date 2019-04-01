package org.dnal.fieldcopy.service.beanutils;

import org.dnal.fieldcopy.core.DefaultFieldFilter;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.core.ServiceFactory;
import org.dnal.fieldcopy.log.SimpleLogger;

public class BUServiceFactory implements ServiceFactory {
	private SimpleLogger logger;

	@Override
	public FieldCopyService createService() {
		FieldRegistry registry = new FieldRegistry();
		DefaultFieldFilter filter = new DefaultFieldFilter();
		BUCopyService copySvc = new BUCopyService(logger, registry, filter);
		return copySvc;
	}
}