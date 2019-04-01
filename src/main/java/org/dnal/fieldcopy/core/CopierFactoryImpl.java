package org.dnal.fieldcopy.core;

import org.dnal.fieldcopy.CopierFactory;
import org.dnal.fieldcopy.FieldCopier;

public class CopierFactoryImpl implements CopierFactory {
	public FieldCopyService copySvc;
	
	public CopierFactoryImpl(FieldCopyService copySvc) {
		this.copySvc = copySvc;
	}
	
	@Override
	public FieldCopier createCopier() {
		return new FieldCopier(copySvc);
	}
}