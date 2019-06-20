package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dnal.fieldcopy.converter.ValueConverter;
import org.dnal.fieldcopy.core.CopySpec;
import org.dnal.fieldcopy.core.FieldCopyService;
import org.dnal.fieldcopy.core.FieldPair;
import org.dnal.fieldcopy.util.ThreadSafeList;

/**
 * First-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class CopyBuilder1A {
	private FieldCopier root;
	private List<String> includeList;
	private List<String> excludeList;
	private boolean doAutoCopy;
	private List<FieldCopyMapping> mappingList;
	private List<ValueConverter> converters;
	private String executionPlanCacheKey;
	private Map<String,Object> additionalSourceValMap;


	public CopyBuilder1A(FieldCopier fieldCopierBuilder) {
		this.root = fieldCopierBuilder;
	}

	/**
	 * (advanced use only). Use this when you are copying the same pair of source and destination
	 * classes in different ways.
	 * @param key Specify a key used to cache the copy instructions.
	 * @return fluent API object.
	 */
	public CopyBuilder1A cacheKey(String key) {
		executionPlanCacheKey = key;
		return this;
	}

	/***
	 * Copy the given set of source fields.  They will be copied to destination fields of the same name.
	 * @param fieldNames fields to copy
	 * @return fluent API object.
	 */
	public CopyBuilder1A include(String...fieldNames) {
		this.includeList = Arrays.asList(fieldNames);
		return this;
	}
	
	/***
	 * Do not copy the given set of source fields. Often used in conjunction with autoCopy to copy all but
	 * a specified list of fields.
	 * @param fieldNames fields not to copy
	 * @return fluent API object.
	 */
	public CopyBuilder1A exclude(String...fieldNames) {
		this.excludeList = Arrays.asList(fieldNames);
		return this;
	}
	
	/***
	 * Use the given value as if it is the value of the given field.
	 * @param name field name
	 * @param value value to use
	 * @return fluent API object.
	 */
	public CopyBuilder1A includeSourceValue(String name, Object value) {
		if (additionalSourceValMap == null) {
			additionalSourceValMap = new HashMap<>();
		}
		additionalSourceValMap.put(name, value);
		return this;
	}

	/***
	 * Copy all matching fields.  That is, if the source and destination classes have a field with the same
	 * name, then copy it.  (Use CopyOptions to control whether fields are matched case-sensitive or not).
	 * @return fluent API object.
	 */
	public CopyBuilder1A autoCopy() {
		this.doAutoCopy = true;
		return this;
	}

	/**
	 * Perform the copy.
	 * @param <T> destination type 
	 * @param destClass type of destination object to create.
	 * @return destination object.
	 */
	public <T> T execute(Class<T> destClass) {
		return doExecute(destClass, null, null, null);
	}

	/**
	 * Use the given set of FieldCopyMappings for sub-objects. When a field is not a scalar value, list, or array,
	 * then an autoCopy is done of its fields, unless a mapping has been provided by calling this method.
	 * @param mappings one or more mapping object.
	 * @return fluent API object.
	 */
	public CopyBuilder1A withMappings(FieldCopyMapping... mappings) {
		if (this.mappingList == null) {
			this.mappingList = new ArrayList<>();
		}
		this.mappingList.addAll(Arrays.asList(mappings));
		return this;
	}
	
	/**
	 * Use the given set of ValueConverters.  When copying a field's value, all converters are asked in turn (by
	 * calling their canConvert method), and the first converter who returns true is used to convert the value.  
	 * @param converters one or more converter object.
	 * @return fluent API object.
	 */
	public CopyBuilder1A withConverters(ValueConverter... converters) {
		if (this.converters == null) {
			this.converters = new ArrayList<>();
		}
		this.converters.addAll(Arrays.asList(converters));
		return this;
	}

	/**
	 * if autocopy then copies matching fields
	 *   -if excludeList non-empty then fields in it are not copied
	 *   -if includeList non-empty then fields not in it are not copied
	 *   -excludeList has priority over includeList
	 *   
	 * -if srList non-empty then those fields are copied
	 * 
	 * @param destClass  type of destination object
	 * @param srcList  source fields to copy
	 * @param destList destination fields to copy to
	 * @param defaultValueList default values to use
	 * @return destination object
	 */

	<T> T doExecute(Class<T> destClass, List<String> srcList, List<String> destList, List<Object> defaultValueList) {
		List<FieldPair> fieldsToCopy = root.buildFieldsToCopy(destClass, doAutoCopy, includeList, 
				excludeList, srcList, destList, defaultValueList, additionalSourceValMap);

		CopySpec spec = new CopySpec();
		spec.sourceObj = root.sourceObj;
		spec.destObj = root.destObj;
		spec.fieldPairs = fieldsToCopy;
		spec.mappingL = mappingList;
		spec.options = root.options;
		spec.additionalSourceValMap = additionalSourceValMap;
		if (this.converters != null) {
			spec.converterL = new ThreadSafeList<ValueConverter>();
			spec.converterL.addAll(this.converters);
		}
		spec.executionPlanCacheKey = executionPlanCacheKey;
		root.mostRecentCopySpec = spec;
		FieldCopyService copySvc = root.getCopyService();
		return copySvc.copyFields(spec, destClass);
	}

	/**
	 * Copy the given field to a destination field of the same name.
	 * @param srcFieldName source field
	 * @return fluent API object.
	 */
	public CopyBuilder2A field(String srcFieldName) {
		return new CopyBuilder2A(this, srcFieldName, srcFieldName, null);
	}
	
	/**
	 * Copy the given field to a destination field of the specified name.
	 * @param srcFieldName source field
	 * @param destFieldName destination field
	 * @return fluent API object.
	 */
	public CopyBuilder2A field(String srcFieldName, String destFieldName) {
		return new CopyBuilder2A(this, srcFieldName, destFieldName, null);
	}
}