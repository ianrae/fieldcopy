package org.dnal.fieldcopy.lambda;

public interface LambdaCallback<T> {
	Object exec(T t);
}