package org.dnal.fieldcopy.core;

import java.util.List;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.log.SimpleLogger;
import org.dnal.fieldcopy.metrics.CopyMetrics;

/**
 * Implementation of the copy operation.  FieldCopy is designed so that different implementations
 * can be used.  
 * @author Ian Rae
 *
 */
public interface FieldCopyService {
	List<FieldPair> buildAutoCopyPairs(TargetPair targetPair);
	FieldDescriptor resolveSourceField(String srcField, TargetPair targetPair);
	void copyFields(CopySpec copySpec);
	<T> T copyFields(CopySpec copySpec, Class<T> destClass);
	void dumpFields(Object sourceObj);
	SimpleLogger getLogger();
	FieldRegistry getRegistry();
	void addBuiltInConverter(ValueConverter converter);
	void setMetrics(CopyMetrics metrics);
	CopyMetrics getMetrics();
	
	/**
	 * Generate unique string that represents the spec. 
	 * This string is used as a cache key so the same copy spec only needs 
	 * to have an execution plan generated once (and then cached).
	 * @param spec
	 * @return
	 */
	String generateExecutionPlanCacheKey(CopySpec spec);
}