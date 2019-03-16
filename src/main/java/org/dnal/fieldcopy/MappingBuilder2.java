package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.List;

/**
 * Second-level fluent API for creating a mapping.
 * 
 * @author Ian Rae
 *
 */
public class MappingBuilder2 {
	private MappingBuilder1 fcb1;
	private List<String> srcList = new ArrayList<>();
	private List<String> destList = new ArrayList<>();
	private List<Object> defaultValueList = new ArrayList<>();

	public MappingBuilder2(MappingBuilder1 fcb1, String srcField, String destField, Object defaultVal) {
		this.fcb1 = fcb1;
		srcList.add(srcField);
		destList.add(destField);
		defaultValueList.add(defaultVal);
	}
	
	public MappingBuilder2 field(String srcFieldName) {
		srcList.add(srcFieldName);
		destList.add(srcFieldName);
		defaultValueList.add(null);
		return this;
	}
	public MappingBuilder2 field(String srcFieldName, String destFieldName) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		defaultValueList.add(null);
		return this;
	}
	public MappingBuilder2 field(String srcFieldName, String destFieldName, Object defaultVal) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		defaultValueList.add(defaultVal);
		return this;
	}
	
	public FieldCopyMapping build() {
		return fcb1.doBuild(srcList, destList, defaultValueList);
	}
}