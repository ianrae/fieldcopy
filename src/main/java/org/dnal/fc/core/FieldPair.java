package org.dnal.fc.core;

public class FieldPair {
	public FieldDescriptor srcProp;
	public String destFieldName;
	public FieldDescriptor destProp; //set lazily
}