package org.dnal.fc.core;

import java.util.List;

import org.dnal.fc.CopyOptions;
import org.dnal.fc.FieldCopyMapping;
import org.dnal.fieldcopy.log.SimpleLogger;

/**
 * Implementation of the copy operation.  FieldCopy is designed so that different implementations
 * can be used.  
 * @author Ian Rae
 *
 */
public interface FieldCopyService {
	List<FieldPair> buildAutoCopyPairs(Class<?> sourceClass, Class<?> destClass);
	void copyFields(CopySpec copySpec);
	void dumpFields(Object sourceObj);
	SimpleLogger getLogger();
	FieldRegistry getRegistry();
}