package org.dnal.fieldvalidate.code;

public class FieldError {
    public String fieldName;
    public Object fieldValue;
    public String errMsg;
    public ErrorType errType;

    public FieldError(String fieldName, Object fieldValue, ErrorType errType) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
        this.errType = errType;
    }
}
