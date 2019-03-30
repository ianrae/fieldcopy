package org.dnal.fieldcopy.converter;

import java.util.List;

import org.dnal.fieldcopy.CopyOptions;
import org.dnal.fieldcopy.FieldCopyMapping;
import org.dnal.fieldcopy.core.BeanDetectorService;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;

/**
 * Context object used by ValueConverters
 * 
 * @author Ian Rae
 *
 */
public class ConverterContext {
	public Class<?>srcClass;
	public Class<?> destClass;
	public FieldCopyService copySvc;
	public CopyOptions copyOptions; 
	//these lists may be null
	public List<FieldCopyMapping> mappingL;
	public List<ValueConverter> converterL;
	public BeanDetectorService beanDetectorSvc;
	
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
