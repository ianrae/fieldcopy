package org.dnal.fc.core;

import org.dnal.fc.FieldCopier;
import org.dnal.fieldcopy.log.SimpleLogger;

/**
 * Creates the FieldCopier and several helper objects.
 * 
 * @author Ian Rae
 *
 */
public interface CopyFactory {
	SimpleLogger createLogger();
	FieldFilter createFieldFilter();
	FieldCopyService createCopyService();
	FieldCopier createCopier();
}