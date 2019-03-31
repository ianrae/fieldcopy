package org.dnal.fieldcopy.planner;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.converter.ValueConverter;

public class ZClassPlan {
	public Class<?> srcClass;
	public Class<?> destClass;
	public List<ZFieldPlan> fieldPlanL = new ArrayList<>();
	public List<ValueConverter> converterL = new ArrayList<>();
}