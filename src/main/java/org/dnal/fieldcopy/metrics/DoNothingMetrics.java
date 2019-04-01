package org.dnal.fieldcopy.metrics;

public class DoNothingMetrics implements CopyMetrics {

	@Override
	public void incrementPlanCount() {
	}
	@Override
	public void incrementLazyPlanGenerationCount() {
	}
	@Override
	public void incrementPlanExecutionCount() {
	}
	@Override
	public void incrementFieldExecutionCount() {
	}
}
