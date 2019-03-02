package org.dnal.fc;

import java.util.ArrayList;
import java.util.List;

//fluent api
public class FCM02 {
	private FCM0 fcb1;
	private List<String> srcList = new ArrayList<>();
	private List<String> destList = new ArrayList<>();

	public FCM02(FCM0 fcb1, String srcField, String destField) {
		this.fcb1 = fcb1;
		srcList.add(srcField);
		destList.add(destField);
	}
	
	public FCM02 field(String srcFieldName) {
		srcList.add(srcFieldName);
		destList.add(srcFieldName);
		return this;
	}
	public FCM02 field(String srcFieldName, String destFieldName) {
		srcList.add(srcFieldName);
		destList.add(destFieldName);
		return this;
	}
	
	public FieldCopyMapping build() {
		return fcb1.doBuild(srcList, destList);
	}
}