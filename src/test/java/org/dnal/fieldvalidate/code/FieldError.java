package org.dnal.fieldvalidate.code;

public class FieldError {
    public String fullTargetPath;
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

    public static String buildTargetPath(Object root, Object target, String fieldName) {
        String targetClass = target.getClass().getSimpleName();
        if (root != null && target != root) {
            String rootClass = root.getClass().getSimpleName();
            targetClass = String.format("%s.%s(%s)", rootClass, fieldName, targetClass);
        }
        return targetClass;
    }
}
