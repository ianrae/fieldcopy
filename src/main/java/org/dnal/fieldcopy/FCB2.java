package org.dnal.fieldcopy;

import java.util.ArrayList;
import java.util.List;

/**
 * Second-level Fluent API for FieldCopy
 * 
 * @author Ian Rae
 *
 */
public class FCB2 {
	private FCB1 fcb1;
	private List<String> srcList = new ArrayList<>();
	private List<String> destList = new ArrayList<>();

	public FCB2(FCB1 fcb1, String srcField, String destField) {
		this.fcb1 = fcb1;
		srcList.add(srcField);
		destList.add(destField);
	}
	
	public FCB2 field(String srcFieldName) {
		srcList.add(srcFieldName);
		destList.add(srcFieldName);
		return this;
	}
	public FCB2 field(String srcFieldName, String destFieldName) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		return this;
	}
	
	public void execute() {
		fcb1.doExecute(srcList, destList);
	}
}