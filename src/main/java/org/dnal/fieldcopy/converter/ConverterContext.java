package org.dnal.fieldcopy.converter;

import java.util.List;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;

public class ConverterContext {
	public String srcFieldName;
	public Class<?>srcClass;
	public Class<?> destClass;
	public FieldCopyService copySvc;
	public CopyOptions copyOptions; 
	
	/**
	 * Create a copy spec for copying the given clazz1 to clazz2
	 * Used when the converter needs to use the copy service.
	 * @return spec
	 */
	public CopySpec createCopySpec(Class<?> clazz1, Class<?> clazz2)  {
		List<FieldPair> fieldPairs = copySvc.buildAutoCopyPairs(clazz1, clazz2);

		CopySpec spec = new CopySpec();
		spec.fieldPairs = fieldPairs;
		spec.options = copyOptions;
		spec.mappingL = null;
		spec.converterL = null;
		return spec;
	}
}
