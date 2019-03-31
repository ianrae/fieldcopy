package org.dnal.fieldcopy.service.beanutils;

import java.util.ArrayList;
import java.util.List;

import org.dnal.fieldcopy.converter.ValueConverter;

public class BUClassPlan {
	public Class<?> srcClass;
	public Class<?> destClass;
	public List<BUFieldPlan> fieldPlanL = new ArrayList<>();
	public List<ValueConverter> converterL = new ArrayList<>();
}