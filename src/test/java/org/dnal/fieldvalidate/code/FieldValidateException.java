package org.dnal.fieldvalidate.code;

public class FieldValidateException extends RuntimeException {
    private FieldError err; //details about the exception

    public FieldValidateException(String message) {
        super(message);
    }
    public FieldValidateException(FieldError err) {
        super(err.errMsg);
        this.err = err;
    }

    public FieldError getErr() {
        return err;
    }
}
