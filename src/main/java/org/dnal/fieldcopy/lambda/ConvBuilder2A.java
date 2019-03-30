package org.dnal.fieldcopy.lambda;


public class ConvBuilder2A<T> {
	private Class<T> clazz;
	private LambdaCallback<T> callback;
	private String destFieldName;
	private boolean notNullFlag = false;
	
	public ConvBuilder2A(ConvBuilder2<T> parent, String destFieldName) {
		this.clazz = parent.clazz;
		this.destFieldName = destFieldName;
	}
	
	public ConvBuilder2A<T> andDestinationField(String destFieldName) {
		this.destFieldName = destFieldName;
		return this;
	}
	
	public ConvBuilder2A<T> andNotNull() {
		this.notNullFlag = true;
		return this;
	}
	
	public ConvBuilder2A<T> thenDo(LambdaCallback<T> callback) {
		this.callback = callback;
		return this;
	}
	
	public LambdaConverter<T> build() {
		return new LambdaConverter<T>(clazz, null, destFieldName, notNullFlag, callback);
	}
}