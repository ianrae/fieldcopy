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

    public static String buildTargetPath(Object root, Object target, String fieldName, Integer index) {
        String fullPath = target.getClass().getSimpleName();

        if (root != null && target != root) {
            String rootClass = root.getClass().getSimpleName();
            fullPath = String.format("%s.%s(%s)", rootClass, fieldName, fullPath);
        }
        if (index != null) {
            fullPath = String.format("%s.%s[%d]", fullPath, fieldName, index);
        } else {
            fullPath = String.format("%s.%s", fullPath, fieldName);
        }
        return fullPath;
    }
}
