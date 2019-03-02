package org.dnal.fc;

import java.util.List;

import org.dnal.fc.core.FieldPair;

public class FieldCopyMapping {
	private Class<?> clazzSrc;
	private Class<?> clazzDest;
	private List<FieldPair> fieldPairs;
	
	public FieldCopyMapping(Class<?> clazzSrc, Class<?> clazzDest) {
		super();
		this.clazzSrc = clazzSrc;
		this.clazzDest = clazzDest;
	}

	public Class<?> getClazzSrc() {
		return clazzSrc;
	}

	public Class<?> getClazzDest() {
		return clazzDest;
	}

	public List<FieldPair> getFieldPairs() {
		return fieldPairs;
	}

	public void setFieldPairs(List<FieldPair> fieldPairs) {
		this.fieldPairs = fieldPairs;
	}
}
