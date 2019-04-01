package org.dnal.fieldcopy.metrics;

public class SimpleCopyMetrics implements CopyMetrics {
	public int planCount;
	public int lazyPlanCount;
	public int execCount;
	public int fieldExecCount;

	@Override
	public void incrementPlanCount() {
		planCount++;
	}

	@Override
	public void incrementLazyPlanGenerationCount() {
		lazyPlanCount++;
	}

	@Override
	public void incrementPlanExecutionCount() {
		execCount++;
	}

	@Override
	public void incrementFieldExecutionCount() {
		fieldExecCount++;
	}

}
