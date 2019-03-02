package org.dnal.fc;

import java.util.ArrayList;
import java.util.List;

/**
 * Second-level fluent API for creating a mapping.
 * 
 * @author Ian Rae
 *
 */
public class MapBuilder2 {
	private MapBuilder1 fcb1;
	private List<String> srcList = new ArrayList<>();
	private List<String> destList = new ArrayList<>();

	public MapBuilder2(MapBuilder1 fcb1, String srcField, String destField) {
		this.fcb1 = fcb1;
		srcList.add(srcField);
		destList.add(destField);
	}
	
	public MapBuilder2 field(String srcFieldName) {
		srcList.add(srcFieldName);
		destList.add(srcFieldName);
		return this;
	}
	public MapBuilder2 field(String srcFieldName, String destFieldName) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		return this;
	}
	
	public FieldCopyMapping build() {
		return fcb1.doBuild(srcList, destList);
	}
}