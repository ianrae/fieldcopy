package org.dnal.fieldcopy.core;

import java.util.List;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.FieldCopyMapping;
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
	<T> T copyFields(CopySpec copySpec, Class<T> destClass);
	void dumpFields(Object sourceObj);
	SimpleLogger getLogger();
	FieldRegistry getRegistry();
}