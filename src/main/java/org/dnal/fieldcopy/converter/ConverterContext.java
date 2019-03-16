package org.dnal.fieldcopy.converter;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.core.FieldCopyService;

public class ConverterContext {
	public String srcFieldName;
	public Class<?>srcClass;
	public Class<?> destClass;
	public FieldCopyService copySvc;
	public CopyOptions copyOptions; 
}
