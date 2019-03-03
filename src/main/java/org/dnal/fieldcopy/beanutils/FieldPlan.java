package org.dnal.fieldcopy.beanutils;

import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.core.ValueTransformer;

public class FieldPlan {
	public FieldPair pair;
	public ValueTransformer transformer;
	public FieldCopyMapping mapping;
}