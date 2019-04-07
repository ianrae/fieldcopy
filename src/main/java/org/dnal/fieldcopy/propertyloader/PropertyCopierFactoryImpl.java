package org.dnal.fieldcopy.propertyloader;

import org.dnal.fieldcopy.CopierFactory;
import org.dnal.fieldcopy.FieldCopier;
import org.dnal.fieldcopy.core.FieldCopyService;

public class PropertyCopierFactoryImpl implements CopierFactory {
	public FieldCopyService copySvc;
	
	public PropertyCopierFactoryImpl(FieldCopyService copySvc) {
		this.copySvc = copySvc;
	}
	
	@Override
	public FieldCopier createCopier() {
		return new FieldCopier(copySvc);
	}
}