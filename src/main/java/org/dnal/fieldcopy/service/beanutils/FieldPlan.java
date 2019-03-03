package org.dnal.fieldcopy.service.beanutils;

import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.converter.ValueTransformer;
import org.dnal.fieldcopy.core.FieldPair;

public class FieldPlan {
	public FieldPair pair;
	public ValueTransformer transformer;
	public FieldCopyMapping mapping;
}