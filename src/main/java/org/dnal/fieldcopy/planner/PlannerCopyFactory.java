package org.dnal.fieldcopy.planner;

import org.dnal.fieldcopy.DefaultCopyFactory;
import org.dnal.fieldcopy.core.CopyFactory;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldFilter;
import org.dnal.fieldcopy.core.FieldRegistry;
import org.dnal.fieldcopy.log.SimpleLogger;

public class PlannerCopyFactory extends DefaultCopyFactory	 {
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
			theSingleton = new PlannerCopyFactory();
		}
		return theSingleton;
	}
}