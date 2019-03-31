package org.dnal.fieldcopy;

import org.dnal.fieldcopy.core.CopyFactory;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.planner.PlannerService;
import org.dnal.fieldcopy.service.beanutils.old.OldDefaultCopyFactory;

public class DefaultCopyFactory extends OldDefaultCopyFactory	 {
	private static CopyFactory theSingleton;

	@Override
	public FieldCopyService createCopyService() {
		SimpleLogger logger = createLogger();
		FieldRegistry registry = new FieldRegistry();
		FieldFilter fieldFilter = createFieldFilter();
		PlannerService copySvc = new PlannerService(logger, registry, fieldFilter);
		return copySvc;
	}
	
	public static CopyFactory Factory() {
		if (theSingleton == null) {
			theSingleton = new DefaultCopyFactory();
		}
		return theSingleton;
	}
}