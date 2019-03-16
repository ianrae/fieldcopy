package org.dnal.fieldcopy.lambda;


public class ConvBuilder1<T> {
	private Class<T> clazz;
	private LambdaCallback<T> callback;
	private String srcFieldName;
	private boolean notNullFlag = false;
	
	public ConvBuilder1(Class<T> clazz, String srcFieldName) {
		this.clazz = clazz;
		this.srcFieldName = srcFieldName;
	}
	
	public ConvBuilder1<T> andNotNull() {
		this.notNullFlag = true;
		return this;
	}
	
	public ConvBuilder1<T> thenDo(LambdaCallback<T> callback) {
		this.callback = callback;
		return this;
	}
	
	public LambdaConverter<T> build() {
		return new LambdaConverter<T>(clazz, srcFieldName, null, notNullFlag, callback);
	}
}