package org.dnal.fieldcopy.service.beanutils;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.util.ThreadSafeList;

/**
 * Plan for copying an object of type srcClass to an object of type destClass.
 * 
 * @author Ian Rae
 *
 */
public class BUClassPlan {
	public Class<?> srcClass;
	public Class<?> destClass;
	public ThreadSafeList<BUFieldPlan> fieldPlanL = new ThreadSafeList<>();
	public ThreadSafeList<ValueConverter> converterL = new ThreadSafeList<>();
}