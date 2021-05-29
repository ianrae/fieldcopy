package org.dnal.fieldvalidate.code;

public class FieldError {
    public String targetClassName;
    public String fieldName;
    public Object fieldValue;
    public String errMsg;
    public ErrorType errType;

    public FieldError(String targetClassName, String fieldName, Object fieldValue, ErrorType errType) {
        this.targetClassName = targetClassName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errType = errType;
    }
}
