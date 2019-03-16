package org.dnal.fieldcopy.lambda;

public class ConvBuilder2<T> {
	Class<T> clazz;
	
	public ConvBuilder2(Class<T> clazz) {
		this.clazz = clazz;
	}
	
	public ConvBuilder2A<T> andDestinationField(String destFieldName) {
		return new ConvBuilder2A<T>(this, destFieldName);
	}
}