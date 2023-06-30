package org.dnal.fieldvalidate.code;

public interface ValidationRule {
    boolean canExecute(ValSpec spec);

    void validate(ValSpec spec, Object fieldValue, ValidationResults res, RuleContext ctx);

    String getName();
}
