package org.dnal.fieldcopy.service.beanutils.old;

import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.FieldPair;

public class FieldPlan {
	public FieldPair pair;
	public ValueConverter converter;
	public FieldCopyMapping mapping;
}