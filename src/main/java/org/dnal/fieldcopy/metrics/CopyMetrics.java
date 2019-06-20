package org.dnal.fieldcopy.metrics;

public interface CopyMetrics {
	void incrementPlanCount();
	void incrementLazyPlanGenerationCount();
	void incrementPlanExecutionCount();
	void incrementFieldExecutionCount();
}
